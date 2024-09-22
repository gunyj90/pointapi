package com.musinsa.pointapi.domain.vo;

public enum PointStatus {
    ACCUMULATED, USED, PARTIALLY_USED, CANCELED;

    public static boolean usable(PointStatus status) {
        return ACCUMULATED.equals(status) || PARTIALLY_USED.equals(status);
    }
}
