package cb.quiz.undomanager.impl;

import cb.quiz.undomanager.iface.IPicoTask;

public class BaseTask implements IPicoTask<Integer> {

    protected boolean mIsRefresh = true;

    protected Integer mInput = 0;
    protected Integer mOp = 0;
    protected Integer mOutput = 0;

    public BaseTask() {
        // DO NOTHING.
    }

    public BaseTask(Integer input) {
        mOutput = mInput = input;
    }

    @Override
    public void setInput(Integer input) {
        if (!mInput.equals(input)) {
            mInput = input;
            mIsRefresh = true;
        }
    }

    @Override
    public Integer getInput() {
        return mInput;
    }

    @Override
    public void setOperand(Integer op) {
        if (!mOp.equals(op)) {
            mOp = op;
            mIsRefresh = true;
        }
    }

    @Override
    public Integer getOperand() {
        return mOp;
    }

    @Override
    public Integer getOutput() {
        return mOutput;
    }
}
