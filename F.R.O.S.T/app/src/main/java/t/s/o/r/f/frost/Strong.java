package t.s.o.r.f.frost;

/**
 * This class should never have existed.
 * @author No one
 * @since Never
 * @Version Only one ever 1.2
 */

public class Strong {
    String strong;
    byte[] strung;
    boolean isStrong = false;
    boolean isStrung = false;

    Strong(String s){
        this.strong = s;
        if (!s.equals(""))
            isStrong = true;
    }
    Strong(byte[] s){
        this.strung = s;
        if (s.length > 1)
            isStrung = true;
    }



}
