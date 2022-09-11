package se.artcomputer.game;

public class GameStaticImpl implements GameRandom {
        private final int[] intSerie;
        int intIndex = 0;
        private final float[] floatSerie;
        int floatIndex = 0;

        public GameStaticImpl(int[] intSerie, float[] floatSerie) {
            this.intSerie = intSerie;
            this.floatSerie = floatSerie;
        }

        @Override
        public int nextInt(int max) {
            int i = intSerie[intIndex++];
            intIndex = intIndex % intSerie.length;
            return i;
        }

        @Override
        public float nextFloat() {
            float v = floatSerie[floatIndex++];
            floatIndex = floatIndex % floatSerie.length;
            return v;
        }

    }