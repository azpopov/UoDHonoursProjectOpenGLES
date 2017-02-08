package arturpopov.basicprojectopengles;

/**
 * Created by arturpopov on 06/02/2017.
 */
public interface IPrimitive
{
    static final int BYTES_PER_FLOAT = 4;
    static final int BYTES_PER_SHORT = 2;

    static final int POSITION_STRIDE_BYTES = 3 * BYTES_PER_FLOAT; //4 is bytes per float
    static final int COLOUR_STRIDE_BYTES = 4 * BYTES_PER_FLOAT;
    static final int POSITION_SIZE = 3;
    static final int COLOUR_SIZE = 4;

    public void draw(int pProgramHandle);

}
