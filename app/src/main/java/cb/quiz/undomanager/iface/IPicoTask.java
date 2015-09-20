package cb.quiz.undomanager.iface;

public interface IPicoTask <T> {

    /**
     * Set the input.
     * @param input The input.
     */
    void setInput(T input);

    /**
     * @return The input.
     */
    T getInput();

    /**
     * Set the operand which is operated with input and result the output.
     * @param op The operand.
     */
    void setOperand(T op);

    /**
     * @return The operand.
     */
    T getOperand();

    /**
     * Calculate the output according to the input.
     * @return The output.
     */
    T getOutput();

}
