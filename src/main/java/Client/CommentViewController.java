package Client;

import Shared.Models.CommentReaction;
import Shared.Models.Reaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.BubbleChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CommentViewController {
    @FXML
    HBox replyHBox;
    @FXML
    TitledPane replyTitledPane;
    @FXML
    WebView commentProfile;
    @FXML
    VBox replyVBox;
    @FXML
    public SVGPath likeIcon;
    @FXML
    public SVGPath dislikeIcon;
    @FXML
    ToggleButton likeCommentButton;
    @FXML
    ToggleButton dislikeCommentButton;
    @FXML
    VBox mainVBox;
    @FXML
    Button replyCommentButton;
    @FXML
    HBox commentHBox;
    @FXML
    HBox commentBelowHBox;
    @FXML
    Button commentButton;
    @FXML
    TextField commentTextField;
    @FXML
    Label authorLabel;
    @FXML
    Label commentTextLabel;
    @FXML
    Label dateLabel;
    public HashMap<Boolean , Short> isCommentLiked;
    private CommentReaction currentReaction;

    long changeInLike = 0;
    private static HBox belowHBox1;
    private static HBox belowHBox2;
    private Rectangle maskProfileRec;
    //Must be used when reply button is clicked. (Tag instead of reply)
    public Boolean isOnReply;
    //To be used when submitting new comment
    private long commentID;

    public void initialize() {
        isOnReply = false;
        replyHBox.getChildren().remove(replyTitledPane);
        //Mask profile
        maskProfileRec = new Rectangle(48, 48);
        maskProfileRec.setArcWidth(48);
        maskProfileRec.setArcHeight(48);
        commentProfile.setClip(maskProfileRec);
        //Icons
        likeCommentButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                likeIcon.setContent("M0 7H3V17H0V7ZM15.77 7H11.54L13.06 2.06C13.38 1.03 12.54 0 11.38 0C10.8 0 10.24 0.24 9.86 0.65L4 7V17H14.43C15.49 17 16.41 16.33 16.62 15.39L17.96 9.39C18.23 8.15 17.18 7 15.77 7Z");
            } else {
                likeIcon.setContent("M18.77 11H14.54L16.06 6.06C16.38 5.03 15.54 4 14.38 4C13.8 4 13.24 4.24 12.86 4.65L7 11H3V21H7H8H17.43C18.49 21 19.41 20.33 19.62 19.39L20.96 13.39C21.23 12.15 20.18 11 18.77 11ZM7 20H4V12H7V20ZM19.98 13.17L18.64 19.17C18.54 19.65 18.03 20 17.43 20H8V11.39L13.6 5.33C13.79 5.12 14.08 5 14.38 5C14.64 5 14.88 5.11 15.01 5.3C15.08 5.4 15.16 5.56 15.1 5.77L13.58 10.71L13.18 12H14.53H18.76C19.17 12 19.56 12.17 19.79 12.46C19.92 12.61 20.05 12.86 19.98 13.17Z");
            }
        });
        dislikeCommentButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                dislikeIcon.setContent("M18,4h3v10h-3V4z M5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21c0.58,0,1.14-0.24,1.52-0.65L17,14V4H6.57 C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14z");
            } else {
                dislikeIcon.setContent("M17,4h-1H6.57C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21 c0.58,0,1.14-0.24,1.52-0.65L17,14h4V4H17z M10.4,19.67C10.21,19.88,9.92,20,9.62,20c-0.26,0-0.5-0.11-0.63-0.3 c-0.07-0.1-0.15-0.26-0.09-0.47l1.52-4.94l0.4-1.29H9.46H5.23c-0.41,0-0.8-0.17-1.03-0.46c-0.12-0.15-0.25-0.4-0.18-0.72l1.34-6 C5.46,5.35,5.97,5,6.57,5H16v8.61L10.4,19.67z M20,13h-3V5h3V13z");
            }
        });
        belowHBox1 = commentHBox;
        belowHBox2 = commentBelowHBox;
        mainVBox.getChildren().remove(commentHBox);
        mainVBox.getChildren().remove(commentBelowHBox);
    }

    public void addReply(Node node) {
        if (!replyHBox.getChildren().contains(replyTitledPane))
            replyHBox.getChildren().addLast(replyTitledPane);
        replyVBox.getChildren().add(node);
    }

    public void cancelCommentAction(ActionEvent event) {
        commentTextField.setText("");
        ((VBox) belowHBox1.getParent()).getChildren().remove(belowHBox1);
        ((VBox) belowHBox2.getParent()).getChildren().remove(belowHBox2);

    }

    public void newCommentAction(ActionEvent event) {

    }

    public void replyOnComment(ActionEvent event) {
        if (belowHBox1.getParent() != null) {
            mainVBox.getChildren().remove(belowHBox1);
            mainVBox.getChildren().remove(belowHBox2);
        }
        mainVBox.getChildren().addLast(belowHBox1);
        mainVBox.getChildren().addLast(belowHBox2);
    }

    public void commentChanged(KeyEvent keyEvent) {
        commentButton.setDisable(commentTextField.getText().isBlank());
    }

    public void setComment(String channel, String text,String date,long like) {
        authorLabel.setText(channel);
        commentTextLabel.setText(text);
        dateLabel.setText(date);
        likeCommentButton.setText(String.valueOf(like));
    }
    public void setCommentLikedState()
    {
        try {
            if (isCommentLiked.get(true) == 1) {
                likeIcon.setContent("M0 7H3V17H0V7ZM15.77 7H11.54L13.06 2.06C13.38 1.03 12.54 0 11.38 0C10.8 0 10.24 0.24 9.86 0.65L4 7V17H14.43C15.49 17 16.41 16.33 16.62 15.39L17.96 9.39C18.23 8.15 17.18 7 15.77 7Z");
                dislikeIcon.setContent("M17,4h-1H6.57C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21 c0.58,0,1.14-0.24,1.52-0.65L17,14h4V4H17z M10.4,19.67C10.21,19.88,9.92,20,9.62,20c-0.26,0-0.5-0.11-0.63-0.3 c-0.07-0.1-0.15-0.26-0.09-0.47l1.52-4.94l0.4-1.29H9.46H5.23c-0.41,0-0.8-0.17-1.03-0.46c-0.12-0.15-0.25-0.4-0.18-0.72l1.34-6 C5.46,5.35,5.97,5,6.57,5H16v8.61L10.4,19.67z M20,13h-3V5h3V13z");
            } else {
                dislikeIcon.setContent("M18,4h3v10h-3V4z M5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21c0.58,0,1.14-0.24,1.52-0.65L17,14V4H6.57 C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14z");
                dislikeIcon.setContent("M18.77 11H14.54L16.06 6.06C16.38 5.03 15.54 4 14.38 4C13.8 4 13.24 4.24 12.86 4.65L7 11H3V21H7H8H17.43C18.49 21 19.41 20.33 19.62 19.39L20.96 13.39C21.23 12.15 20.18 11 18.77 11ZM7 20H4V12H7V20ZM19.98 13.17L18.64 19.17C18.54 19.65 18.03 20 17.43 20H8V11.39L13.6 5.33C13.79 5.12 14.08 5 14.38 5C14.64 5 14.88 5.11 15.01 5.3C15.08 5.4 15.16 5.56 15.1 5.77L13.58 10.71L13.18 12H14.53H18.76C19.17 12 19.56 12.17 19.79 12.46C19.92 12.61 20.05 12.86 19.98 13.17Z");
            }
        } catch (Exception ex) {
            likeIcon.setContent("M18.77 11H14.54L16.06 6.06C16.38 5.03 15.54 4 14.38 4C13.8 4 13.24 4.24 12.86 4.65L7 11H3V21H7H8H17.43C18.49 21 19.41 20.33 19.62 19.39L20.96 13.39C21.23 12.15 20.18 11 18.77 11ZM7 20H4V12H7V20ZM19.98 13.17L18.64 19.17C18.54 19.65 18.03 20 17.43 20H8V11.39L13.6 5.33C13.79 5.12 14.08 5 14.38 5C14.64 5 14.88 5.11 15.01 5.3C15.08 5.4 15.16 5.56 15.1 5.77L13.58 10.71L13.18 12H14.53H18.76C19.17 12 19.56 12.17 19.79 12.46C19.92 12.61 20.05 12.86 19.98 13.17Z");
            dislikeIcon.setContent("M17,4h-1H6.57C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21 c0.58,0,1.14-0.24,1.52-0.65L17,14h4V4H17z M10.4,19.67C10.21,19.88,9.92,20,9.62,20c-0.26,0-0.5-0.11-0.63-0.3 c-0.07-0.1-0.15-0.26-0.09-0.47l1.52-4.94l0.4-1.29H9.46H5.23c-0.41,0-0.8-0.17-1.03-0.46c-0.12-0.15-0.25-0.4-0.18-0.72l1.34-6 C5.46,5.35,5.97,5,6.57,5H16v8.61L10.4,19.67z M20,13h-3V5h3V13z");
        }

    }
    public void likeCommentAction(ActionEvent event)
    {

    }
}
