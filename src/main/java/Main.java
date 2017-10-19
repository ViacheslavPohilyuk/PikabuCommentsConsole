import java.io.IOException;
import java.util.Scanner;

/**
 * Created by mac on 13.10.17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String nickname = "";
        System.out.println("Введите имя пользователя:");
        Scanner sc = new Scanner(System.in);
        nickname = sc.next().trim();

        UserCommentsRecognizer commentsRecognizer = new UserCommentsRecognizer(nickname);
        String googleSearchCommentsQuery = commentsRecognizer.checkUserProfileInfo();
        commentsRecognizer.recognizeUserComments(googleSearchCommentsQuery);
    }
}
