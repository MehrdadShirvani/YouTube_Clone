package Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.web.WebView;

import java.io.IOException;

public class ChannelViewController {
    @FXML
    ToggleButton subsButton;
    @FXML
    SVGPath subsIcon;
    @FXML
    WebView profileWebView;
    @FXML
    WebView myNoContentWebView;
    @FXML
    WebView myNoContentWebView2;
    @FXML
    WebView myNoContentWebView3;
    @FXML
    WebView otherNoContentWebView;
    @FXML
    WebView otherNoContentWebView2;
    @FXML
    WebView otherNoContentWebView3;
    @FXML
    VBox homeVBox;
    @FXML
    VBox contentVBox;
    @FXML
    HBox recentHBox;
    @FXML
    HBox popularHBox;
    @FXML
    FlowPane videosFlowPane;
    private Rectangle maskcommentProfileRec;

    public void initialize() throws IOException {
        //Mask
        maskcommentProfileRec = new Rectangle(80, 80);
        maskcommentProfileRec.setArcWidth(80);
        maskcommentProfileRec.setArcHeight(80);
        profileWebView.setClip(maskcommentProfileRec);

        //Icon
        subsButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                subsIcon.setContent("M10 20H14C14 21.1 13.1 22 12 22C10.9 22 10 21.1 10 20ZM20 17.35V19H4V17.35L6 15.47V10.32C6 7.40001 7.56 5.10001 10 4.34001V3.96001C10 2.54001 11.49 1.46001 12.99 2.20001C13.64 2.52001 14 3.23001 14 3.96001V4.35001C16.44 5.10001 18 7.41001 18 10.33V15.48L20 17.35ZM19 17.77L17 15.89V10.42C17 7.95001 15.81 6.06001 13.87 5.32001C12.61 4.79001 11.23 4.82001 10.03 5.35001C8.15 6.11001 7 7.99001 7 10.42V15.89L5 17.77V18H19V17.77Z");
            } else {
                subsIcon.setContent("");
            }
        });
        String myURL = HomeController.class.getResource("my-no-content.html").toExternalForm();
        String otherURL = HomeController.class.getResource("other-no-content.html").toExternalForm();
        System.out.println(myURL);

        myNoContentWebView.getEngine().load(myURL);
        otherNoContentWebView.getEngine().load(otherURL);
        myNoContentWebView.setPageFill(Color.TRANSPARENT);
        otherNoContentWebView.setPageFill(Color.TRANSPARENT);

        myNoContentWebView2.getEngine().load(myURL);
        otherNoContentWebView2.getEngine().load(otherURL);
        myNoContentWebView2.setPageFill(Color.TRANSPARENT);
        otherNoContentWebView2.setPageFill(Color.TRANSPARENT);

        myNoContentWebView3.getEngine().load(myURL);
        otherNoContentWebView3.getEngine().load(otherURL);
        myNoContentWebView3.setPageFill(Color.TRANSPARENT);
        otherNoContentWebView3.setPageFill(Color.TRANSPARENT);

        for (int i = 0; i < 10; ++i) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
            recentHBox.getChildren().add(fxmlLoader.load());
            ((SmallVideoView)fxmlLoader.getController()).setPref(0.8,false,false);
        }
        for (int i = 0; i < 10; ++i) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
            popularHBox.getChildren().add(fxmlLoader.load());
            ((SmallVideoView)fxmlLoader.getController()).setPref(0.8,false,false);
        }
        for (int i = 0; i < 10; ++i) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
            videosFlowPane.getChildren().add(fxmlLoader.load());
            ((SmallVideoView)fxmlLoader.getController()).setPref(1,false,false);
        }

        //Delete the vboxs if the content is found(my/other)
        //If no content is found then delete the contentVBox
        //If content is found:
//        homeVBox.setAlignment(Pos.CENTER_LEFT);

    }
}