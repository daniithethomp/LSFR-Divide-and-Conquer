package lab1;
public class LSFR {
    private int size;
    private String state;
    private int[] tapSequence;

    public LSFR(int size, String state, int[] tapSequence) {
        this.size = size;
        this.state = state;
        this.tapSequence = tapSequence;
    }

    public LSFR(int size, int[] tapSequence) {
        this.size = size;
        this.state = "0".repeat(size);
        this.tapSequence = tapSequence;
    }

    public int getNextBit() {
        char[] state = this.state.toCharArray(); // 0 1 1 0
        int feedbackBit = 0;
        for (int tap : tapSequence) {
            feedbackBit ^= Character.getNumericValue(state[tap]);
        }
        String newState = String.valueOf(feedbackBit) + this.state.substring(0, this.size-1);
        this.state = newState;
        return Character.getNumericValue(state[this.size - 1]);
    }

    public int[] getTapSequence() {
        return tapSequence;
    }

    public void setTapSequence(int[] taps) {
        this.tapSequence = taps;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static void main(String[] args) {
        LSFR lsfr = new LSFR(4, "0110", new int[]{0,3});
        System.out.println("Iteration | State | Output Bit");
        System.out.println("-----------------------------");
        System.out.println(0 + "         |  " + lsfr.getState() + "  |    " + "-");
        for (int i = 0; i < 20; i ++) {
            int outputBit = lsfr.getNextBit();
            System.out.println((i + 1) + "         |  " + lsfr.getState() + "  |    " + outputBit);
        }
    }
}

