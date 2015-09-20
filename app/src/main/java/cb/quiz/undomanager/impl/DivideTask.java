package cb.quiz.undomanager.impl;

public class DivideTask extends BaseTask {

    public DivideTask(Integer op) {
        if (op == 0) {
            throw new IllegalArgumentException("Could not divide by 0.");
        }

        setOperand(op);
    }

    @Override
    public Integer getOutput() {
        if (mIsRefresh) {
            mOutput = mInput / mOp;
            mIsRefresh = false;
        }
        return mOutput;
    }

}
