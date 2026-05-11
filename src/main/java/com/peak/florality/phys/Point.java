package com.peak.florality.phys;

public record Point(int x, int y, int z, boolean relative) {
    public Point withX(int newX) {
        return new Point(newX, this.y, this.z, this.relative);
    }

    public Point withY(int newY) {
        return new Point(this.x, newY, this.z, this.relative);
    }

    public Point withZ(int newZ) {
        return new Point(this.x, this.y, newZ, this.relative);
    }

    public Point withRelative(boolean newRelative) {
        return new Point(this.x, this.y, this.z, newRelative);
    }

    public Point shiftRelative(boolean newRelative) {
        return new Point(this.x, this.y, this.z, !newRelative);
    }
}
