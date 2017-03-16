package com.jetbrains.edu.learning;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.edu.learning.courseFormat.Task;
import com.jetbrains.edu.learning.editor.StudyChoiceVariantsPanel;
import com.jetbrains.edu.learning.ui.StudyToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StudyFileEditorManagerListener implements FileEditorManagerListener {
  private final StudyToolWindow myToolWindow;
  private final Project myProject;

  public StudyFileEditorManagerListener(StudyToolWindow toolWindow, Project project) {
    myToolWindow = toolWindow;
    myProject = project;
  }

  @Override
  public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
    Task task = getTask(file);
    setTaskText(task, StudyUtils.getTaskDir(file));
    if (task != null) {
      if (task.isChoiceTask()) {
        final StudyChoiceVariantsPanel choicePanel = new StudyChoiceVariantsPanel(task);
        myToolWindow.setBottomComponent(choicePanel);
      }
      else {
        myToolWindow.setBottomComponent(null);
      }
    }
  }

  @Override
  public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
    for (VirtualFile openedFile : source.getOpenFiles()) {
      if (StudyUtils.getTaskFile(myProject, openedFile) != null) {
        return;
      }
    }
    myToolWindow.setEmptyText(myProject);
  }

  @Override
  public void selectionChanged(@NotNull FileEditorManagerEvent event) {
    VirtualFile file = event.getNewFile();
    if (file != null) {
      Task task = getTask(file);
      setTaskText(task, StudyUtils.getTaskDir(file));
    }
    myToolWindow.setBottomComponent(null);
  }

  @Nullable
  private Task getTask(@NotNull VirtualFile file) {
    return StudyUtils.getTaskForFile(myProject, file);
  }

  private void setTaskText(@Nullable final Task task, @Nullable final VirtualFile taskDirectory) {
    String text = StudyUtils.getTaskTextFromTask(taskDirectory, task);
    if (text == null) {
      myToolWindow.setEmptyText(myProject);
      return;
    }
    myToolWindow.setTaskText(text, taskDirectory, myProject);
  }
}