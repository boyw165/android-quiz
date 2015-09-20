import junit.framework.Assert;

import org.junit.Test;

import cb.quiz.undomanager.UndoManager;

public class TestUndoManager {

    @Test
    public void Case01() {
        UndoManager manager = new UndoManager(100, 10);
        manager.add(150);       // 250
        manager.subtract(50);   // 200
        manager.divide(2);      // 100
        manager.multiply(4);    // 400

        Assert.assertEquals(400, manager.getNumber());
        manager.undo();
        Assert.assertEquals(100, manager.getNumber());
        manager.undo();
        Assert.assertEquals(200, manager.getNumber());
        manager.undo();
        Assert.assertEquals(250, manager.getNumber());
        manager.undo();
        Assert.assertEquals(100, manager.getNumber());
        manager.redo();
        Assert.assertEquals(250, manager.getNumber());
        manager.redo();
        Assert.assertEquals(200, manager.getNumber());
        manager.undo();
        Assert.assertEquals(250, manager.getNumber());
    }

    @Test
    public void Case02() {
        UndoManager manager = new UndoManager(1, 3);
        manager.add(1); // 2
        manager.add(1); // 3
        manager.add(1); // 4
        manager.add(1); // 5

        Assert.assertEquals(5, manager.getNumber());
        manager.undo();
        Assert.assertEquals(4, manager.getNumber());
        manager.undo();
        Assert.assertEquals(3, manager.getNumber());
        manager.undo();
        Assert.assertEquals(2, manager.getNumber());
        manager.undo();
        // The first operation should be erase because the history size is only 3.
        Assert.assertEquals(2, manager.getNumber());
    }

    @Test
    public void Case03() {
        // testing the conner case of redo operation
        UndoManager manager = new UndoManager(1, 2);
        manager.add(1); // 2
        manager.add(1); // 3

        // do nothing
        manager.redo();

        Assert.assertEquals(3, manager.getNumber());
        manager.undo();
        Assert.assertEquals(2, manager.getNumber());
        manager.undo();
        Assert.assertEquals(1, manager.getNumber());
        manager.redo();
        Assert.assertEquals(2, manager.getNumber());
        manager.redo();
        Assert.assertEquals(3, manager.getNumber());
        manager.redo(); // do nothing
        Assert.assertEquals(3, manager.getNumber());
    }

}
