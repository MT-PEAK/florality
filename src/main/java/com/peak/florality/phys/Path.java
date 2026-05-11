package com.peak.florality.phys;

import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {
    private final List<Point> points;

    public Path(Point... points) {
        List<Point> temp = new ArrayList<>();
        Collections.addAll(temp, points);
        this.points = Collections.unmodifiableList(temp);
    }

    public List<Point> getPoints() {
        return points;
    }

    public List<Vector3d> toVectors() {
        List<Vector3d> vectors = new ArrayList<>();
        for (Point p : points) {
            vectors.add(new Vector3d(p.x(), p.y(), p.z()));
        }
        return Collections.unmodifiableList(vectors);
    }

    public Path addPoints(Point... newPoints) {
        List<Point> combined = new ArrayList<>(this.points);
        Collections.addAll(combined, newPoints);
        return new Path(combined.toArray(new Point[0]));
    }

    public Path translate(Vector3d offset) {
        List<Point> translated = new ArrayList<>();
        for (Point p : points) {
            Vector3d v = new Vector3d(p.x(), p.y(), p.z()).add(offset);
            translated.add(new Point((int)v.x, (int)v.y, (int)v.z, p.relative()));
        }
        return new Path(translated.toArray(new Point[0]));
    }

    public Vector3d getVectorBetween(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= points.size() || toIndex < 0 || toIndex >= points.size()) {
            throw new IndexOutOfBoundsException("Invalid point indices");
        }
        Point from = points.get(fromIndex);
        Point to = points.get(toIndex);
        return new Vector3d(to.x() - from.x(), to.y() - from.y(), to.z() - from.z());
    }

    public static Vector3d getVectorBetween(Point a, Point b) {
        return new Vector3d(b.x() - a.x(), b.y() - a.y(), b.z() - a.z());
    }

    @Override
    public String toString() {
        return "Path" + points;
    }
}
