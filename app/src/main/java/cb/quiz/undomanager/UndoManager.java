package cb.quiz.undomanager;

import java.util.ArrayList;
import java.util.ListIterator;

import cb.quiz.undomanager.iface.IPicoTask;
import cb.quiz.undomanager.impl.AddTask;
import cb.quiz.undomanager.impl.BaseTask;
import cb.quiz.undomanager.impl.DivideTask;
import cb.quiz.undomanager.impl.MultiplyTask;
import cb.quiz.undomanager.impl.SubtractTask;

public class UndoManager {

    // The maximum size of the history.
    protected int mSizeMax = 0;

    // The 1st task in the history.
    protected IPicoTask<Integer> mInit;

    // The history.
    protected ArrayList<IPicoTask<Integer>> mHistory = new ArrayList<>();

    protected int mLast = 0;

    public UndoManager(int number, int historySize) {
        if (number == Integer.MIN_VALUE || number == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Initial number cannot be equal to " +
                    "MAX_VALUE/MIN_VALUE");
        }
        if (historySize <= 0 || historySize == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("History size cannot be either negative or " +
                    "equal to MAX_VALUE.");
        }

        mInit = new BaseTask(number);
        mHistory.add(mInit);

        mSizeMax = historySize + 1;
    }

    public int getNumber() {
        return mHistory.get(mLast).getOutput();
    }

    public void undo() {
        if (--mLast < 0) {
            mLast = 0;
        }
    }

    public void redo() {
        if (++mLast >= mHistory.size()) {
            mLast = mHistory.size() - 1;
        }
    }

    public void add(int num) {
        addTask(new AddTask(num));
    }

    public void subtract(int num) {
        addTask(new SubtractTask(num));
    }

    public void multiply(int num) {
        addTask(new MultiplyTask(num));
    }

    public void divide(int num) {
        addTask(new DivideTask(num));
    }

    @Override
    public String toString() {
        String ret = super.toString().concat("\n");

        for (int i = 0, max = mLast + 1; i < max; ++i) {
            IPicoTask<Integer> task = mHistory.get(i);
            ret = ret.concat("input=" + task.getInput() +
                    "; operand=" + task.getOperand() +
                    "; ouput=" + task.getOutput() +
                    "\n");
        }

        return ret;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Protected/Pirvate ///////////////////////////////////////////////////////////////////////////

    protected int addTask(IPicoTask<Integer> task) {
        // Remove remaining tasks after index of mLast.
        try {
            ListIterator<IPicoTask<Integer>> it = mHistory.listIterator(mLast + 1);

            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        } catch (IndexOutOfBoundsException e) {
            // DO NOTHING.
        }

        // Add new task.
        mHistory.add(task);

        // Trim the history according to the maximum size.
        while (mHistory.size() > mSizeMax) {
            mHistory.remove(0);
        }

        // Update the last index.
        mLast = mHistory.size() - 1;

        // Compute result.
        int output = 0;
        for (int i = 0; i < mHistory.size(); ++i) {
            IPicoTask<Integer> currTask = mHistory.get(i);

            if (i == 0) {
                // The first task has only output.
                output = currTask.getOutput();
            } else {
                // Set last output to current input.
                currTask.setInput(output);
                // Update output.
                output = currTask.getOutput();
            }
        }

        return output;
    }

}
