package ar.gob.coronavirus.utils.many;

public class SemaforoInfo {

    private String[] EmojiUnicodes = {
            "0", "1", "2", "3",
            "4", "5", "6", "7",
            "8", "9", "A", "B",
            "C", "D", "E", "F"
    };

    private int semilla;

    public SemaforoInfo(int semilla) {
        this.semilla = semilla;
    }

    public int getSemilla() {
        return semilla;
    }

    public int getPosicion1() {
        return (semilla & 0x0F00) / 0x100;
    }

    public int getPosicion2() {
        return (semilla & 0x0F0) / 0x10;
    }

    public int getPosicion3() {
        return semilla & 0x0F;
    }

    public String getEmoji1(){
        return EmojiUnicodes[getPosicion1()];
    }
    public String getEmoji2(){
        return EmojiUnicodes[getPosicion2()];
    }
    public String getEmoji3(){
        return EmojiUnicodes[getPosicion3()];
    }

}
