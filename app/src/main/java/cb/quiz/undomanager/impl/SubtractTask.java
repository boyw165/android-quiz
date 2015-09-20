package cb.quiz.undomanager.impl;

public class SubtractTask extends BaseTask {

    public SubtractTask(Integer op) {
        setOperand(op);
    }

    @Override
    public Integer getOutput() {
        if (mIsRefresh) {
            mOutput = mInput - mOp;
            mIsRefresh = false;
        }
        return mOutput;
    }

}
