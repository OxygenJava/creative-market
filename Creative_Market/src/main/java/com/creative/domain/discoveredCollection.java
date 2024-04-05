package com.creative.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class discoveredCollection {
    private Integer id;
    private Integer userId;
    private Integer discoveredId;
    private LocalDateTime collectionTime;
}
