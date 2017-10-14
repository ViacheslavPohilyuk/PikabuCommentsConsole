import java.io.IOException;
import java.util.Scanner;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;

/**
 * Created by mac on 13.10.17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String nickname = "";
        System.out.println("Введите имя пользователя:");
        Scanner sc = new Scanner(System.in);
        nickname = sc.next().trim();

        Document doc = null;
        try {
            long beginTime = System.currentTimeMillis();
            doc = Jsoup.connect("https://pikabu.ru/profile/" + nickname).get();

            // Checking the existence of a user's profile
            String title = doc.title();
            if (title.equals("404. Страница не найдена")) {
                System.out.println("Такого пользователя не существует!");
            }

            // Recognizing the gender of a user
            String gender = "";
            Elements genderImgList = doc.select("img[title^=пол:]");
            if (!genderImgList.isEmpty()) {
                String elemImgGenderValue = genderImgList.get(0).attr("title");
                int indexOfSpace = elemImgGenderValue.indexOf(" ");
                gender = elemImgGenderValue.substring(indexOfSpace + 1);
                System.out.println("User's gender: " + gender);
            }

            // Find out the count of user's comments
            Element elemCommentsCount = doc.select("span:contains(комментариев) + span").get(0);
            String commentsCount = elemCommentsCount.html();
            if (commentsCount.equals("0")) {
                System.out.println("У пользователя нет комментариев!");
                System.exit(0);
            }
            System.out.println("User's comments count: " + commentsCount);

            double runTimeSeconds = (System.currentTimeMillis() - beginTime) / 1000D;
            System.out.println("Run time: " + runTimeSeconds + " seconds");
        } catch (HttpStatusException ex) {
            System.err.println("Недопустимые символы в имени пользователя!");
        }


    }
}
