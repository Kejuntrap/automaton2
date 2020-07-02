import java.util.*;

public class Starmaton {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashMap<String, State> fa = new HashMap<String, State>();         //状態の集合 Q に当たる
        HashSet<String> alphabets = new HashSet<String>();     //使う文字列の集合　\Sigma に当たる  空文字列はEPS
        int numAlphabet = sc.nextInt();
        for (int itr = 0; itr < numAlphabet; itr++) {
            String s = sc.next();
            alphabets.add(s);
        }
        alphabets.add("EPS");
        int numState = sc.nextInt();      //状態数 タマの数
        int numTransition = sc.nextInt();      //遷移数 要するに矢印の数
        for (int itr = 0; itr < numState; itr++) {      //状態の情報を読み込む
            String stateName = sc.next();
            boolean isAccepted = sc.nextBoolean();
            boolean isStart = sc.nextBoolean();
            State addState = new State(stateName, 0, isAccepted, isStart);
            fa.put(stateName, addState);
        }
        for (int itr = 0; itr < numTransition; itr++) {     //遷移の情報を読み込む
            String transitionFrom = sc.next();
            String transitionChar = sc.next();
            String transitionTo = sc.next();
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
        fa = starnize(fa);      //スター演算対応化する
        testOutput(fa);     //オートマトンの格納状態を確認します
    }

    private static HashMap<String, State> starnize(HashMap<String, State> fa) {
        String startstate = "startstate";
        State s = new State(startstate, 0, false, true);
        String startname = "";
        for (String st : fa.keySet()) {
            if (fa.get(st).isStart) {
                fa.get(st).isStart = false;
                startname = st;
            }
        }
        ArrayList<String> fixedstart = new ArrayList<String>();
        fixedstart.add(startname);
        s.transition.put("EPS",fixedstart);
        fa.put(startstate,s);
        for(String st:fa.keySet()){
            if(fa.get(st).isAccepted){
                if(fa.get(st).transition.containsKey("EPS")){
                    fa.get(st).transition.get("EPS").add(startname);
                }else{
                    ArrayList<String> t = new ArrayList<String>();
                    t.add(startname);
                    fa.get(st).transition.put("EPS",t);
                }
            }
        }
        return fa;
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
            this.transition = new HashMap<String, ArrayList<String>>();
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
            ki = "\"" + ki + "\"";
            if (s.isStart) {
                lp("\tempty -> " + ki + ";");
            }
            HashMap<String, String> destination = new HashMap<String, String>();
            for (String c : s.transition.keySet()) {
                ArrayList<String> t = s.transition.get(c);
                for (String ss : t) {
                    ss = "\"" + ss + "\"";
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


    private static class Path {
        private int readchar;       //計算している文字位置
        private int fromDepth;      //1つ前の深さ
        private int nowDepth;       //今の深さ
        private String fromState;       //1つ前のstate
        private String nowState;    //現在居るstate

        Path(int readchar, int fromDepth, int nowDepth, String fromState, String nowState) {
            this.readchar = readchar;
            this.fromDepth = fromDepth;
            this.nowDepth = nowDepth;
            this.fromState = fromState;
            this.nowState = nowState;
        }
    }

    static void lp(Object o) {
        System.out.println(o);
    }
}
