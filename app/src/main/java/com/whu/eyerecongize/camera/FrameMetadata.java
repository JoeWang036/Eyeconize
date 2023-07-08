

package com.whu.eyerecongize.camera;

public class FrameMetadata {
    private final int width;

    private final int height;

    private final int rotation;

    private final int cameraFacing;

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int getCameraFacing() {
        return this.cameraFacing;
    }

    private FrameMetadata(int width, int height, int rotation, int facing) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.cameraFacing = facing;
    }

    public static class Builder {
        private int width;

        private int height;

        private int rotation;

        private int cameraFacing;

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setRotation(int rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setCameraFacing(int facing) {
            this.cameraFacing = facing;
            return this;
        }

        public FrameMetadata build() {
            return new FrameMetadata(this.width, this.height, this.rotation, this.cameraFacing);
        }
    }
}
