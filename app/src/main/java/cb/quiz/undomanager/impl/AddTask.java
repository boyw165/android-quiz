package cb.quiz.undomanager.impl;

public class AddTask extends BaseTask {

    public AddTask(Integer op) {
        setOperand(op);
    }

    @Override
    public Integer getOutput() {
        if (mIsRefresh) {
            mOutput = mInput + mOp;
            mIsRefresh = false;
        }
        return mOutput;
    }

}
