import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class View extends Application {
    private Group root = new Group();
    private MediaView viewer;
    private Rectangle rectangle;
    private Image loadingImg = new Image(new FileInputStream("./resources/ajax_loader.gif"));
    private ImageView loading = new ImageView(loadingImg);
    private Label errorMessage = new Label();
    private final ExecutorService exec = Executors.newCachedThreadPool();

    public View() throws FileNotFoundException {
    }

    public static void main(String[] args) {
        Application.launch(View.class, args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("To52 face finder");
        Scene scene = new Scene(root, 1280, 600, Color.web("daebf3"));
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final FileChooser fileChooser = new FileChooser();
        //<Rectangle>
        rectangle = new Rectangle(scene.getWidth(), scene.getHeight());
        rectangle.setFill(Color.web("262626"));
        rectangle.setOpacity(0.9);
        rectangle.setVisible(false);
        loading.setVisible(false);
        loading.setLayoutX((scene.getWidth()/2)-(loadingImg.getWidth()/2));
        loading.setLayoutY((scene.getHeight()/2)-(loadingImg.getHeight()/2));
        loading.setOpacity(0.6);
        //</Rectangle>
        //<VBox>
        VBox vBox = new VBox();
        vBox.setLayoutX(75);
        vBox.setLayoutY(125);
        //</VBox>
        //<Label>
        errorMessage.setStyle("-fx-font-size: 2.5em; -fx-font-weight: bold;");
        errorMessage.setVisible(false);
        Label title = new Label("To52 face finder");
        title.setStyle("-fx-font-size: 2.5em; -fx-font-weight: bold; -fx-padding: 50 20 50 20;");
        title.setLayoutX(60);
        Label subTitle = new Label("Video :");
        subTitle.setStyle("-fx-font-size: 2.5em; -fx-font-weight: bold; -fx-padding: 20 0 0 0;");
        Label inputDirectory = new Label("Input directory :");
        Label outputDirectory = new Label("Output directory :");
        Label enableCorection = new Label("Enable image auto-correction ?");
        Label videoPathLabel = new Label("Video path : ");
        //</Label>
        //<TestField>
        final TextField path1 = new TextField ();
        final TextField path2 = new TextField ();
        final TextField videoPath = new TextField ();
        path1.setPrefWidth(scene.getWidth()/1.7);
        path2.setPrefWidth(scene.getWidth()/1.7);
        videoPath.setPrefWidth(scene.getWidth()/1.7);
        //</TestField>
        //<CheckBox>
        CheckBox box = new CheckBox();
        //</CheckBox<
        //<Button>
        Button setDefault = new Button("Set default directory");
        setDefault.setOnAction(event -> {
            path1.setText(System.getProperty("user.dir") + "\\input_media");
            path2.setText(System.getProperty("user.dir") +"\\out\\video\\");
            box.setSelected(false);
        });
        Button setDefaultVideoPath = new Button("Set default video path");
        setDefaultVideoPath.setOnAction(event -> {
            videoPath.setText(System.getProperty("user.dir") + "\\out\\video\\test.mp4");
        });
        Button parcourir1 = new Button("Parcourir");
        parcourir1.setOnAction(event -> {
            String string = directoryChooser.showDialog(primaryStage).getPath();
            path1.setText(string);
        });
        Button parcourir2 = new Button("Parcourir");
        parcourir2.setOnAction(event -> {
            String string = directoryChooser.showDialog(primaryStage).getPath();
            path2.setText(string);
        });
        Button parcourir3 = new Button("Parcourir");
        parcourir3.setOnAction(event -> {
            String string = fileChooser.showOpenDialog(primaryStage).getPath();
            videoPath.setText(string);
        });
        Button process = new Button("Process");
        process.setLayoutX(85);
        process.setLayoutY(250);
        process.setOnAction(event -> {
            rectangle.setVisible(true);
            loading.setVisible(true);
            if (path1.getText()==""){path1.setText(System.getProperty("user.dir") + "\\input_media");}
            if (path2.getText()==""){path2.setText(System.getProperty("user.dir") +"\\out\\video\\");}
            try {
                this.launchVideoMaking(path1.getText(),path2.getText(), box.isSelected());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Button launchVideo = new Button("Launch Video");
        launchVideo.setOnAction(event -> {
            try {
                if(videoPath.getText().equals("")){
                    launchVideo(System.getProperty("user.dir") + "\\out\\video\\", "test.mp4");
                }else {
                    Path p = Paths.get(videoPath.getText());
                    launchVideo(p.getParent().toString(),p.getFileName().toString());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        Button launchFaceFinder = new Button("Launch Face Finder");
        launchFaceFinder.setOnAction(event -> {
            if(videoPath.getText().equals("")){
                videoPath.setText(System.getProperty("user.dir") + "\\out\\video\\test.mp4");
            }
            buildTask(System.getProperty("user.dir") + "\\venv1\\Scripts\\python.exe " + System.getProperty("user.dir") + "\\inference_video_face.py " + videoPath.getText());
        });
        //</Button>
        //<Gride>
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(5);
        grid.setHgap(5);
        GridPane.setConstraints(inputDirectory, 0, 0);
        GridPane.setConstraints(outputDirectory, 0, 1);
        GridPane.setConstraints(path1, 1, 0);
        GridPane.setConstraints(path2, 1, 1);
        GridPane.setConstraints(parcourir1, 2, 0);
        GridPane.setConstraints(parcourir2, 2, 1);
        GridPane.setConstraints(enableCorection,0,2);
        GridPane.setConstraints(box,1,2);
        GridPane.setConstraints(setDefault,2,2);
        GridPane.setConstraints(process,0,3);
        GridPane.setConstraints(subTitle,0,5);
        GridPane.setConstraints(videoPathLabel,0,6);
        GridPane.setConstraints(videoPath,1,6);
        GridPane.setConstraints(parcourir3,2,6);
        GridPane.setConstraints(setDefaultVideoPath,2,7);
        GridPane.setConstraints(launchVideo,0,7);
        GridPane.setConstraints(launchFaceFinder,1,7);
        grid.getChildren().addAll(path1,path2,inputDirectory, outputDirectory,parcourir1,parcourir2,enableCorection,box,setDefault,launchVideo,process,
                subTitle,videoPathLabel,videoPath,parcourir3,setDefaultVideoPath,launchFaceFinder);
        vBox.getChildren().add(grid);
        //</Gride>
        root.getChildren().addAll(vBox,title,rectangle,loading,errorMessage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String executeCommand(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            System.out.println(command);
            p = Runtime.getRuntime().exec(command);
            //p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    private void launchVideoMaking(String inputDir, String outputDir, Boolean isSelected) throws IOException {
        String command;
        if (isSelected){
            command = System.getProperty("user.dir") + "\\venv1\\Scripts\\python.exe " + System.getProperty("user.dir") + "\\image2Video.py -i " + inputDir + " -o " + outputDir + " -c";
        }else {
            command = System.getProperty("user.dir") + "\\venv1\\Scripts\\python.exe " + System.getProperty("user.dir") + "\\image2Video.py -i " + inputDir + " -o " + outputDir;
        }
        buildTask(command);
    }

    private void launchVideo(String Dir, String fileName) throws MalformedURLException {
        if(fileName.endsWith(".mp4")){
            File f = new File(Dir, fileName);
            Media media = new Media(f.toURI().toURL().toString());
            MediaPlayer player = new MediaPlayer(media);
            viewer = new MediaView(player);
            player.setOnReady(() -> {
                player.setStartTime(Duration.ZERO);
                player.setStopTime(media.getDuration());
            });
            player.setOnEndOfMedia(() -> player.stop());
            StackPane secondaryLayout = new StackPane();
            secondaryLayout.getChildren().add(viewer);
            Scene secondScene = new Scene(secondaryLayout, media.getWidth(), media.getHeight());
            Stage newWindow = new Stage();
            newWindow.setTitle("Video");
            newWindow.setScene(secondScene);
            newWindow.show();
            newWindow.setFullScreen(true);
            player.play();
        }else{
            errorMessage.setText("This app only accept the .mp4 video");
            errorMessage.setVisible(true);
        }
    }

    private void buildTask(String command){
        Task<String> commandTask = new Task<String>() {
            @Override
            protected String call() {
                return executeCommand(command);
            }
        };
        commandTask.setOnSucceeded(event -> {
            // this is executed on the FX Application Thread,
            // so it is safe to update the UI here if you need
            System.out.println("Process complet");
            this.rectangle.setVisible(false);
            this.loading.setVisible(false);
            File logs = new File("./log/appLogs.txt");
            try {
                FileWriter fileWriter = new FileWriter(logs, true);
                fileWriter.write(  "\n" + command + "\n" + new java.util.Date() + "\n" +commandTask.getValue());
                System.out.println("Log file have been updated");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        commandTask.setOnFailed(event -> {
            commandTask.getException().printStackTrace();
        });
        exec.execute(commandTask);
    }
}