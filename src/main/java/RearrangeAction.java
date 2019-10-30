import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RearrangeAction extends AnAction {
    public RearrangeAction() {
        super("Rearrange");
    }

    public void actionPerformed(AnActionEvent event) {
        this.rearrangePaths(event);
        this.rearrangeSchemas(event);
    }

    @Override
    public void update(@NotNull final AnActionEvent e) {
        final Project project = e.getProject();
        VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String fileType = vFile.getFileType().getName();
        e.getPresentation().setEnabledAndVisible(project != null && fileType.equals("YAML"));
    }

    private void rearrangePaths(AnActionEvent event) {
        Project project = event.getProject();
        final Editor editor = event.getData(CommonDataKeys.EDITOR);

        Document document = editor.getDocument();
        String text = document.getText();
        int start = text.indexOf("paths:\n  ") + 7;
        int end = text.indexOf("components:\n  ");

        String[] paths = text.substring(start, end).split(" {2}/");
        Arrays.sort(paths);
        String sortedText = String.join("  /", paths);
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, sortedText)
        );

    }

    private void rearrangeSchemas(AnActionEvent event) {
        Project project = event.getProject();
        final Editor editor = event.getData(CommonDataKeys.EDITOR);

        Document document = editor.getDocument();
        String text = document.getText() + "\n";
        int start = text.indexOf("schemas:\n    ") + 9;
        int end = text.length();

        String[] schemas = text.substring(start, end).split(" {4}(?=[A-Z])");
        Arrays.sort(schemas);
        String sortedText = String.join("    ", schemas);
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end - 1, sortedText.substring(0, sortedText.length() - 1))
        );
    }
}