package arturpopov.basicprojectopengles;

/**
 * Created by arturpopov on 06/02/2017.
 */
interface IPrimitive
{
    int BYTES_PER_FLOAT = 4;
    int BYTES_PER_SHORT = 2;

    int POSITION_STRIDE_BYTES = 3 * BYTES_PER_FLOAT; //4 is bytes per float
    int COLOUR_STRIDE_BYTES = 4 * BYTES_PER_FLOAT;
    int POSITION_SIZE = 3;
    int COLOUR_SIZE = 4;

    void draw(int pProgramHandle);

}
