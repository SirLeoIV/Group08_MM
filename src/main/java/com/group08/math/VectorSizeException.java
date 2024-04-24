package com.group08.math;

public class VectorSizeException extends RuntimeException {
    public VectorSizeException(Vector v1, Vector v2) {
        super(
                "Vector sizes do not match: " + v1.getSize() + " and " + v2.getSize() + " respectively"
        );
    }
}
