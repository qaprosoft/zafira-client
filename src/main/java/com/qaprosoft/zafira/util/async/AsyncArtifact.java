package com.qaprosoft.zafira.util.async;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
final class AsyncArtifact {

    private CompletableFuture<String> urlFuture;
    private String name;
    private Integer expiresIn;

}
