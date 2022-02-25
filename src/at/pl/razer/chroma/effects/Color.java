package at.pl.razer.chroma.effects;

import java.util.function.BiFunction;

public class Color {

    public static int r(int rgb) {
        return (rgb >> 2*8) & 0xFF;
    }

    public static int g(int rgb) {
        return (rgb >> 1*8) & 0xFF;
    }

    public static int b(int rgb) {
        return (rgb >> 0*8) & 0xFF;
    }

    public static int rgb(int r, int g, int b) {
        return (r << (2*8)) | (g << (1*8)) | (b << (0*8));
    }

    public static int bgr(int r, int g, int b) {
        return (b << (2*8)) | (g << (1*8)) | (r << (0*8));
    }

    public static int rgb2bgr(int rgb) {
        return bgr(r(rgb), g(rgb), b(rgb));
    }

    public static int darker(int rgb) {
        return darker(rgb, 1);
    }

    public static int darker(int rgb, int amount) {
        return rgb(Math.max(0, r(rgb) - amount), Math.max(0, g(rgb) - amount), Math.max(0, b(rgb) - amount));
    }

    public static int darker(int rgb, double ratio) {
        return rgb((int) Math.max(0, r(rgb) * ratio), (int) Math.max(0, g(rgb) - ratio), (int) Math.max(0, b(rgb) - ratio));
    }

    public static int[][] fill(int[][] map, BiFunction<Integer, Integer, Integer> function) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                map[row][col] = function.apply(row, col);
            }
        }
        return map;
    }

}
