package cb.quiz.undomanager.impl;

public class MultiplyTask extends BaseTask {

    public MultiplyTask(Integer op) {
        setOperand(op);
    }

    @Override
    public Integer getOutput() {
        if (mIsRefresh) {
            mOutput = mInput * mOp;
            mIsRefresh = false;
        }
        return mOutput;
    }
}
