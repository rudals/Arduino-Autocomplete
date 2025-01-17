package processing.app;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;

import processing.app.syntax.SketchTextArea;
import cc.arduino.packages.autocomplete.SketchCompletionProvider;

public class EditorListener implements KeyListener {
  
  private Editor editor;
  
  public EditorListener(Editor editor) {
    super();
    this.editor = editor;
  }
  
  /** ctrl-alt on windows and linux, cmd-alt on mac os x */
  private static final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  private static final int CTRL_ALT = InputEvent.ALT_MASK | CTRL;
  private static final int CTRL_SHIFT = InputEvent.SHIFT_MASK | CTRL;

  public void keyTyped(KeyEvent event) {
    char c = event.getKeyChar();

    if ((event.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
      // The char is not control code when CTRL key pressed? It should be a shortcut.
      if (!Character.isISOControl(c)) {
        event.consume();
      }
    }
  }

  @Override
  public void keyPressed(KeyEvent event) {

    SketchTextArea textarea = editor.getTextArea();
    
    if (!textarea.isEditable()) return;
    
    Sketch sketch = editor.getSketch();

    int code = event.getKeyCode();
    
    // Navigation..
    if ((event.getModifiers() & CTRL) == CTRL && code == KeyEvent.VK_TAB) {
      sketch.handleNextCode();
    }

    // Navigation..
    // FIXME: not working on LINUX !!!
    if ((event.getModifiers() & CTRL_SHIFT) == CTRL_SHIFT && code == KeyEvent.VK_TAB) {
        sketch.handlePrevCode();
    }
    
    // Navigation..
    if ((event.getModifiers() & CTRL_ALT) == CTRL_ALT) {
      if (code == KeyEvent.VK_LEFT) {
        sketch.handlePrevCode();
      } else if (code == KeyEvent.VK_RIGHT) {
        sketch.handleNextCode();
      }
    }
    
//    if (event.isAltDown() && code == KeyEvent.VK_T) {
//      int line = textarea.getCaretLineNumber();
//      textarea.setActiveLineRange(line, line + 3); 
//    }

    // Generate New Variable
    if (event.isAltDown() && code == KeyEvent.VK_ENTER) {
      
      int line = textarea.getCaretLineNumber();
      
      Token tokenListForLine = textarea.getTokenListForLine(line);
      int start = RSyntaxUtilities.getNextImportantToken(tokenListForLine, textarea, line).getOffset();
      int end = textarea.getLineEndOffsetOfCurrentLine();
      
      try {
        String expression = textarea.getText(start, end - start);
        SketchCompletionProvider provider = textarea.getCompletionProvider();
        provider.generateNewVariableFor(expression, start);
        
        
        
      } catch (BadLocationException e) {}
      
    }    
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // TODO Auto-generated method stub
    
  }

}
