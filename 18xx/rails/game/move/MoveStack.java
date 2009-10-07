/* $Header: /Users/blentz/rails_rcs/cvs/18xx/rails/game/move/MoveStack.java,v 1.1 2009/10/07 19:00:38 evos Exp $
 *
 * Created on 17-Jul-2006
 * Change Log:
 */
package rails.game.move;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rails.game.ReportBuffer;
import rails.util.LocalText;

/**
 * This class represent one game's complete "move stack", which is a list
 * of MoveSets. Each MoveSet contains the (low-level) changes caused by
 * one particular player action.
 * <p>The only purpose of the move stack is to enable Undo and Redo.
 * @author Erik Vos
 */
public class MoveStack {

    private MoveSet currentMoveSet = null;
    private List<MoveSet> moveStack = new ArrayList<MoveSet>();
    private int lastIndex = -1;
    private boolean enabled = false;

    protected static Logger log =
            Logger.getLogger(MoveStack.class.getPackage().getName());

    public MoveStack () {
    }

    /**
     * Start making moves undoable. Will be called once, after all
     * initialisations are complete.
     */
    public void enable() {
        enabled = true;
    }

    public boolean start(boolean undoableByPlayer) {
        log.debug(">>> Start MoveSet(index=" + (lastIndex + 1) + ")");
        if (currentMoveSet == null) {
            currentMoveSet = new MoveSet(undoableByPlayer);
            while (lastIndex < moveStack.size() - 1) {
                moveStack.remove(moveStack.size() - 1);
            }
            return true;
        } else {
            log.warn("MoveStack is already open");
            return false;
        }
    }

    public boolean finish() {
        log.debug("<<< Finish MoveSet (index=" + (lastIndex + 1) + ")");
        if (currentMoveSet == null) {
            log.warn("No action open for finish");
            return false;
        } else if (currentMoveSet.isEmpty()) {
            log.warn("Action to finish is empty and will be discarded");
            currentMoveSet = null;
            return true;
        } else {
            moveStack.add(currentMoveSet);
            lastIndex++;
            currentMoveSet = null;
            return true;
        }
    }

    public boolean cancel() {
        if (currentMoveSet != null) {
            currentMoveSet.unexecute();
            currentMoveSet = null;
            return true;
        } else {
            log.warn("No action open for cancel");
            return false;
        }
    }

    public boolean addMove (Move move) {

        if (!enabled) return true;

        if (currentMoveSet != null) {
            currentMoveSet.addMove(move);
            return true;
        } else {
            log.warn("No MoveSet open for " + move);
            return false;
        }
    }

    public void setLinkedToPrevious() {
        if (currentMoveSet != null) {
            currentMoveSet.setLinkedToPrevious();
        } else {
            log.warn("No MoveSet open");
        }
    }

    public boolean undoMoveSet (boolean forced) {
        if (lastIndex >= 0 && lastIndex < moveStack.size()
        		&& (forced || moveStack.get(lastIndex).isUndoableByPlayer())
        		&& currentMoveSet == null) {
            MoveSet undoAction;
            do {
                ReportBuffer.add(LocalText.getText("UNDO"));
                // log.debug ("MoveStack undo index is "+lastIndex);
                undoAction = moveStack.get(lastIndex--);
                undoAction.unexecute();
            } while (undoAction.isLinkedToPrevious());
            return true;
        } else {
            log.error("Invalid undo: index=" + lastIndex + " size="
                      + moveStack.size() + " currentAction=" + currentMoveSet
                      + " forced=" + forced + " byPlayer="
                      + isUndoableByPlayer());
            return false;
        }
    }

    public boolean redoMoveSet () {
        if (currentMoveSet == null && lastIndex < moveStack.size() - 1) {
            ReportBuffer.add(LocalText.getText("REDO"));
            (moveStack.get(++lastIndex)).reexecute();
            // log.debug ("MoveStack redo index is "+lastIndex);
            return true;
        } else {
            log.error("Invalid redo: index=" + lastIndex + " size="
                      + moveStack.size());
            return false;
        }
    }

    public boolean isUndoableByPlayer() {
        return lastIndex >= 0 && moveStack.get(lastIndex).isUndoableByPlayer();
    }

    public boolean isRedoable() {
        return lastIndex < moveStack.size() - 1;
    }

    public boolean isUndoableByManager() {
        return lastIndex >= 0;
    }

    public boolean isOpen() {
        return currentMoveSet != null;
    }

}
