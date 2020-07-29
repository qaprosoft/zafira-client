package com.qaprosoft.zafira.util;

import com.qaprosoft.zafira.models.dto.TestArtifactType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestArtifactHolder {

    private static final Set<TestArtifactType> TEST_ARTIFACTS = Collections.synchronizedSet(new HashSet<>());

    public static void add(TestArtifactType artifact) {
        TEST_ARTIFACTS.add(artifact);
    }

    public static Set<TestArtifactType> getAndClear() {
        Set<TestArtifactType> artifacts = new HashSet<>(TEST_ARTIFACTS);
        TEST_ARTIFACTS.clear();
        return artifacts;
    }

}
