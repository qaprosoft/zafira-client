package com.qaprosoft.zafira.util.upload;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.internal.SdkBufferedInputStream;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.qaprosoft.zafira.client.BasicClient;
import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.client.ZafiraSingleton;
import com.qaprosoft.zafira.models.dto.auth.TenantType;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.CompletableFuture;

import static com.qaprosoft.zafira.util.AsyncUtil.get;

class AwsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsService.class);

    private static final ZafiraClient zafiraClient;
    private static final BasicClient client;
    private static CompletableFuture<AmazonS3> amazonClient;
    private static SessionCredentials amazonS3SessionCredentials;

    static {
        zafiraClient = ZafiraSingleton.INSTANCE.getClient();
        client = zafiraClient.getClient();
    }

    private AwsService() {
    }

    /**
     * Uploads file to Amazon S3 used integration data from server
     * @param file - any file to upload
     * @param expiresIn - in seconds to generate presigned URL
     * @param keyPrefix - bucket folder name where file will be stored
     * @return url of the file in string format
     * @throws Exception throws when there are any issues with a Amazon S3 connection
     */
    static String uploadFile(File file, Integer expiresIn, String keyPrefix) throws Exception {
        String filePath = null;
        TenantType tenantType = client.getTenantType();
        if (getAmazonClient() != null && tenantType != null && !StringUtils.isBlank(tenantType.getTenant())) {
            String fileName = RandomStringUtils.randomAlphanumeric(20) + "." + FilenameUtils.getExtension(file.getName());
            String relativeKey = keyPrefix + fileName;
            String key = tenantType.getTenant() + relativeKey;

            try (SdkBufferedInputStream stream = new SdkBufferedInputStream(new FileInputStream(file), (int) (file.length() + 100))) {
                String type = Mimetypes.getInstance().getMimetype(file.getName());

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(type);
                metadata.setContentLength(file.length());

                PutObjectRequest putRequest = new PutObjectRequest(amazonS3SessionCredentials.getBucket(), key, stream, metadata);
                getAmazonClient().putObject(putRequest);
                CannedAccessControlList controlList = tenantType.isUseArtifactsProxy() ? CannedAccessControlList.Private
                        : CannedAccessControlList.PublicRead;
                getAmazonClient().setObjectAcl(amazonS3SessionCredentials.getBucket(), key, controlList);

                filePath = tenantType.isUseArtifactsProxy() ? client.getRealServiceUrl() + relativeKey : getFilePath(key);

            } catch (Exception e) {
                LOGGER.error("Can't save file to Amazon S3", e);
            }
        } else {
            throw new Exception("Can't save file to Amazon S3. Verify your credentials or bucket name");
        }

        return filePath;
    }

    private static String getFilePath(String key) {
        return getAmazonClient().getUrl(amazonS3SessionCredentials.getBucket(), key).toString();
    }

    /**
     * Registers Amazon S3 client
     */
    private static CompletableFuture<AmazonS3> initAmazonS3Client() {
        amazonClient = CompletableFuture.supplyAsync(() -> {
            amazonS3SessionCredentials = zafiraClient.getAmazonSessionCredentials().getObject();
            AmazonS3 client = null;
            if (amazonS3SessionCredentials != null) {
                try {
                    client = AmazonS3ClientBuilder.standard()
                                                  .withCredentials(
                                                          new AWSStaticCredentialsProvider(new BasicSessionCredentials(amazonS3SessionCredentials.getAccessKeyId(),
                                                                  amazonS3SessionCredentials.getSecretAccessKey(), amazonS3SessionCredentials.getSessionToken())))
                                                  .withRegion(Regions.fromName(amazonS3SessionCredentials.getRegion())).build();
                    if (!client.doesBucketExistV2(amazonS3SessionCredentials.getBucket())) {
                        throw new Exception(
                                String.format("Amazon S3 bucket with name '%s' doesn't exist.", amazonS3SessionCredentials.getBucket()));
                    }
                } catch (Exception e) {
                    LOGGER.error("Amazon integration is invalid. Verify your credentials or region.", e);
                }
            }
            return client;
        });
        return amazonClient;
    }

    private static AmazonS3 getAmazonClient() {
        return get(amazonClient, AwsService::initAmazonS3Client);
    }

}
