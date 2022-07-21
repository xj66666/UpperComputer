package decode;

public class Adpcm_state {
    int valprev;      /* Previous output value */
    int index;        /* Index into stepsize table */

    public Adpcm_state() {
    }

    public Adpcm_state(int valprev, int index) {
        this.valprev = valprev;
        this.index = index;
    }

}
