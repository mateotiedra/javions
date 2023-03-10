package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
/**
 * This class is used to compute the power of the radio sample.
 *
 * @author Kevan Lam (356395)
 */
public final class PowerWindow {
    private final PowerComputer powerComputer;
    private boolean ArrayB = false;
    private final int batchsize = 1 << 16;
    private int batch;
    private final int windowSize;
    private long position = 0;
    private int[] batchA = new int[batchsize];
    private int[] batchB = new int[batchsize];
    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        Preconditions.checkArgument(windowSize > 0 && windowSize<= batchsize);
        this.powerComputer = new PowerComputer(stream,batchsize);
        this.windowSize = windowSize;
        batch = powerComputer.readBatch(batchA);
    }
    // Get size
    public int size(){
        return windowSize;
    }
    // Get position
    public long position(){
        return position;
    }
    public boolean isFull(){
        return position + windowSize > batch;
    }
    public int get(int i){
        if(i < 0 || i >= windowSize){throw new IndexOutOfBoundsException("Index invalide :" + i);}
        if ((position % batchsize + i) > batchsize){
            return batchB[(int)(position % batchsize + i - batchsize)];
        }else{
            return batchA[(int)(position % batchsize + i)];
        }
    }
    public void advance() throws IOException{
        position++;
        if((position % batchsize)+windowSize >= batchsize && !ArrayB){
            batch += powerComputer.readBatch(batchB);
            ArrayB = true;
        }
        if(position % batchsize == 0 && position!=0){
            System.arraycopy(batchB,0,batchA,0,batchsize);
            ArrayB = false;
        }
    }
    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset >= 0);
        while(offset>0){
            advance();
            offset--;
        }
    }
}

