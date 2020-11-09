package com.example.huffman;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity {

    public static PriorityQueue<Node> queue;
    public static HashMap<Character, String> charToCode = new HashMap<Character, String>();
    String text;
    TextView text1, text2, text3;
    ImageView image;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        image = findViewById(R.id.image);

        BitmapDrawable drawable = (BitmapDrawable)image.getDrawable();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap src = BitmapFactory.decodeResource(getResources(),R.drawable.sample, options);
        Bitmap resized = Bitmap.createScaledBitmap(src, 700, 616, true);

        String Str =BitmapToString(resized); //이미지를 문자로 변경
        text = (new BigInteger(Str.getBytes())).toString(16); //문자를 16진수로 변경
        main();
        long bytes = text.length() * 4; //16진수의 한자릿수 크기는 4bit
        long size = resized.getWidth() * resized.getHeight() * 24/8; //사진의 크기 : 너비 x 높이 x 비트수/8
        String size1 = android.text.format.Formatter.formatFileSize(this, size); //이미지 크기
        String size2 = android.text.format.Formatter.formatFileSize(this, bytes); //16진수 크기
        text1.setText(size1);
        text2.setText(size2);
    }
    public String BitmapToString(Bitmap bitmap){ //이미지를 문자로 변경
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }
    public static Node huffmanCoding(int n){
        for (int i = 0; i<n-1; i++){
            Node z = new Node();
            z.right = queue.poll(); //우측에 배치
            z.left = queue.poll(); //좌측에 배치
            z.frequency = z.right.frequency + z.left.frequency; //합을 큐에 다시 넣음
            queue.add(z);
        }
        return queue.poll();
    }
    public void main(){
        HashMap<Character, Integer>dictionary = new HashMap<Character, Integer>(); //dictionary라는 hashmap에는 값, 갯수
        for (int i = 0; i<text.length(); i++){ //글자들 갯수 새는 반복문
            char temp = text.charAt(i); //temp안에 text의 i번째 자릿값 넣기
            if (dictionary.containsKey(temp)) //hashmap에 temp자리에 같은 값이 있다면 갯수 +1
                dictionary.put(temp, dictionary.get(temp)+1);
            else //없다면 갯수는 1개, 새로 생성
                dictionary.put(temp, 1);
        }
        queue = new PriorityQueue<Node>(100, new FrequencyComparator());
        int number = 0;

        for (Character c : dictionary.keySet()){
            Node temp = new Node();
            temp.character = c; //노드 메소드의 char에 dictionary의 키값 넣기
            temp.frequency = dictionary.get(c); //그에 맞는 frequency 넣기
            queue.add(temp); //q에 이 노드 넣기
            number++; //넣은 양 체크
        }
        Node root = huffmanCoding(number); //좌측, 우측 배치 후 큐에 다시 넣기
        traversal(root, new String()); //2진수 변환
        result = new String();
//        for (int i = 0; i< text.length(); i++){
//            result = result + charToCode.get(text.charAt(i)) + " ";
//            Log.e("Result", String.valueOf(android.text.format.Formatter.formatFileSize(this, result.length())));
//        }
        text3.setText(String.valueOf(android.text.format.Formatter.formatFileSize(this, text.length())));
    }
    public static void traversal(Node n, String s){
        if (n == null)
            return;
        traversal(n.left, s+"0");
        traversal(n.right, s + "1");
        charToCode.put(n.character, s);
    }
}
class Node{
    public char character;
    public int frequency;
    public Node left, right;
}
class FrequencyComparator implements Comparator<Node>{
    @Override
    public int compare(Node a, Node b) {
        int frequencyA = a.frequency;
        int frequencyB = b.frequency;
        return frequencyA - frequencyB;
    }
}