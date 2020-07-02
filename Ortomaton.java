import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Ortomaton {        //和集合演算のオートマトンを構築
    static Scanner sc=new Scanner(System.in);
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        HashSet<String> alphabets1 = new HashSet<String>();     //使う文字列の集合　\Sigma に当たる  空文字列はEPS
        HashMap<String, State> fa1 = new HashMap<String, State>();         //状態の集合 Q に当たる

        HashSet<String> alphabets2 = new HashSet<String>();     //使う文字列の集合　\Sigma に当たる  空文字列はEPS
        HashMap<String, State> fa2 = new HashMap<String, State>();         //状態の集合 Q に当たる

        alphabets1 = inputAlphabets();  //アルファベットの入力
        fa1 = inputAutomaton("N1");

        alphabets2 = inputAlphabets();  //アルファベットの入力
        fa2 = inputAutomaton("N2");


        HashMap<String,State> ormaton = calcsum(fa1,fa2);
        testOutput(ormaton);

    }
    private static HashMap<String,State> calcsum(HashMap<String, State> fa1,HashMap<String, State> fa2){
        String[] fas = new String[2];
        for(String s:fa1.keySet()){
            if(fa1.get(s).isStart){
                fas[0] = s;
                fa1.get(s).isStart=false;
                break;
            }
        }
        for(String s:fa2.keySet()){
            if(fa2.get(s).isStart){
                fas[1] = s;
                fa2.get(s).isStart=false;
                break;
            }
        }
        String startstate="startstate";
        State s = new State(startstate,0,false , true);
        HashMap<String,State> ormaton = new HashMap<String,State>();
        ArrayList<String> ars = new ArrayList<String>();
        for(int i=0; i<2; i++){
            ars.add(fas[i]);
        }
        s.transition.put("EPS",ars);
        ormaton.put(startstate,s);
        for(String ss : fa1.keySet()){
            ormaton.put(ss,fa1.get(ss));
        }
        for(String ss : fa2.keySet()){
            ormaton.put(ss,fa2.get(ss));
        }
        return ormaton;
    }
    private static class Path {
        int readchar;       //計算している文字位置
        int fromDepth;      //1つ前の深さ
        int nowDepth;       //今の深さ
        String fromState;       //1つ前のstate
        String nowState;    //現在居るstate

        Path(int readchar, int fromDepth, int nowDepth, String fromState, String nowState) {
            this.readchar = readchar;
            this.fromDepth = fromDepth;
            this.nowDepth = nowDepth;
            this.fromState = fromState;
            this.nowState = nowState;
        }
    }
    static private HashSet<String> inputAlphabets(){
        HashSet<String> ss=new HashSet<String>();
        int numAlphabet = sc.nextInt();
        for (int itr = 0; itr < numAlphabet; itr++) {
            String s = sc.next();
            ss.add(s);
        }
        ss.add("EPS");  //和集合演算のときに要る
        return ss;
    }
    static private HashMap<String,State> inputAutomaton(String prefix){
        HashMap<String, State> fa = new HashMap<String, State>();         //状態の集合 Q に当たる
        int numState = sc.nextInt();      //状態数 タマの数
        int numTransition = sc.nextInt();      //遷移数 要するに矢印の数
        for (int itr = 0; itr < numState; itr++) {      //状態の情報を読み込む
            String stateName = sc.next();
            boolean isAccepted = sc.nextBoolean();
            boolean isStart = sc.nextBoolean();
            stateName=prefix+":"+stateName;
            State addState = new State(stateName, 0, isAccepted, isStart);
            fa.put(stateName, addState);
        }
        for (int itr = 0; itr < numTransition; itr++) {     //遷移の情報を読み込む
            String transitionFrom = sc.next();
            String transitionChar = sc.next();
            String transitionTo = sc.next();
            transitionFrom=prefix+":"+transitionFrom;
            transitionTo=prefix+":"+transitionTo;
            if (transitionChar.equals("EPS")) {     //空文字列
                if (fa.get(transitionFrom).equals(fa.get(transitionTo))) {
                    //EPSの遷移で遷移前と遷移後で同じ状態を指していると無限ループになる
                } else {
                    (fa.get(transitionFrom)).addTransition(transitionChar, transitionTo); //遷移を追加
                }
            } else {
                (fa.get(transitionFrom)).addTransition(transitionChar, transitionTo); //遷移を追加
            }
        }
        return fa;
    }

    static void lp(Object o) {
        System.out.println(o);
    }

    private static class State {    // \delta , q , F  を定義
        private String name = "";     //状態遷移図のときの○の中身
        private int depth = 0;        //オートマトンの遷移の深さ　図を書くときに要るかも
        private boolean isAccepted = false;   //◎かどうか
        private boolean isStart = false;      //始点かどうか
        private HashMap<String, ArrayList<String>> transition;  //遷移先   入力文字と遷移先

        State(String name, int depth, boolean isAccepted, boolean isStart) {
            this.name = name;
            this.depth = depth;
            this.isAccepted = isAccepted;
            this.isStart = isStart;
            this.transition = new HashMap<String, ArrayList<String>>();     //
        }

        void addTransition(String readchar, String destination) {//HashMap<String,String>だと読み込む文字(key)の行き先を複数指定できず、DFAしか実装できない hashmap<String,ArrayList<string(行き先)>>にした
            if (!this.transition.containsKey(readchar)) {        //読み込む文字が未定義の場合
                if ((this.transition.get(readchar)) == null || (this.transition.get(readchar)).size() == 0) {
                    ArrayList<String> tmp = new ArrayList<String>();
                    tmp.add(destination);
                    this.transition.put(readchar, tmp);
                }
            } else {
                ArrayList<String> tmp = this.transition.get(readchar);
                tmp.add(destination);
                this.transition.put(readchar, tmp);
            }
        }
    }
    static void testOutput(HashMap<String, State> automa) {// for graphiz
        lp("digraph G {");
        lp("\tempty [label = \"\" shape = plaintext];");
        for (String ki : automa.keySet()) {
            if (!automa.get(ki).isAccepted) {
                lp("\t\"" + ki + "\";");
            } else {
                lp("\t\"" + ki + "\" [shape = doublecircle];");
            }
        }
        lp("");
        for (String ki : automa.keySet()) {
            State s = automa.get(ki);
            ki = "\""+ki+"\"";
            if (s.isStart) {
                lp("\tempty -> " + ki + ";");
            }
            HashMap<String, String> destination = new HashMap<String, String>();
            for (String c : s.transition.keySet()) {
                ArrayList<String> t = s.transition.get(c);
                for (String ss : t) {
                    ss = "\""+ss+"\"";
                    if (c.equals("EPS")) {
                        if (destination.containsKey((ki + " -> " + ss))) {
                            destination.put(ki + " -> " + ss, destination.get(ki + " -> " + ss) + ",ε");
                        } else {
                            destination.put(ki + " -> " + ss, "ε");
                        }
                    } else {
                        if (destination.containsKey((ki + " -> " + ss))) {
                            destination.put(ki + " -> " + ss, destination.get(ki + " -> " + ss) + "," + c);
                        } else {
                            destination.put(ki + " -> " + ss, c);
                        }
                    }
                }
            }
            for (String ss : destination.keySet()) {
                lp("\t" + ss + " [label =\"" + destination.get(ss) + "\"];");
            }
        }
        lp("}");
    }
}
