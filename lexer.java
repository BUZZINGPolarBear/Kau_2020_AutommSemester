import java.io.*;

public class lexer
{
    public static BufferedReader userCmd;

    public static Token toToken;
    public static final String letters = "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "$@#";//연산과 관련없는 특수문자는 추가 가능함.
    public static final String digits = "0123456789.";

    public static void main(String args[])
    {
        ReadFile();
    }

    private static void ReadFile()
    {
        try
        {
            //src폴더에 Clite구문을 위치시키고, 한줄씩 읽으면서 Lexer 구현하기
            File file = new File("src\\Clite.txt");
            FileReader fileReader = new FileReader(file);
            userCmd=new BufferedReader(fileReader);
            String cmdLine = "";
            int cnt = 1;

            while((cmdLine = userCmd.readLine())!=null)
            {//한줄씩 읽으면서 lexer구현
                int indexToken = 0;
                Token[] tokens = new Token[10000000];
                System.out.println("*************************\nLine " + cnt++ + " " + cmdLine);

                for (int i = 0; i < cmdLine.length(); i++)
                {
                    if(Character.isLetter(cmdLine.charAt(i)))
                    {
                        String spelling = concat(lexer.letters+lexer.digits, cmdLine, i); //letters+digits 범위의 변수만 토큰화하기, 명령어, 첫 인덱스
                        switch(spelling)
                        {
                            case "int":
                                tokens[indexToken] = new Token("Int", "VarType");
                                break;
                            case "float":
                                tokens[indexToken] = new Token("Float", "VarType");
                                break;
                            case "char":
                                tokens[indexToken] = new Token("Char", "VarType");
                                break;
                            case "for": case "while":
                                tokens[indexToken] = new Token("Interation", spelling);
                                break;
                            case "if": case "else":
                                if(cmdLine.charAt(i+2) == 'i' && cmdLine.charAt(i+3) == 'f')
                                {//else if인 경우
                                    tokens[indexToken] = new Token("Conditional State", "else if");
                                    spelling = "else if";
                                    break;
                                }
                                tokens[indexToken] = new Token("Conditional State", spelling);
                                break;
                            default:
                                tokens[indexToken] = new Token("Id", spelling);
                                break;
                        }
                        if(spelling.length()>1) {i += spelling.length()-1;}//같은 단어 벗어나기
                        indexToken++;
                    }
                    else if(Character.isDigit(cmdLine.charAt(i)))
                    {
                        String number = concat(digits + ".", cmdLine, i); // 소수에 대비해 . 추가
                        if(number.contains("."))
                        {//소수인 경우
                            tokens[indexToken] = new Token("FloatLiteral", number);
                        }
                        else
                        {//정수인 경우
                            tokens[indexToken] = new Token("IntLiteral", number);
                        }
                        if(number.length()>1) {i += number.length()-1;}//같은 단어 벗어나기
                        indexToken++;
                    }
                    else
                    {
                        switch (cmdLine.charAt(i))
                        {
                            case ' ': case '\t': case '\r': case ',': //빈공간, 컴마시 인덱스 증가시키기
                                indexToken--;
                                break;
                            case '/':// 나누기 혹은 주석문자
                                if(cmdLine.charAt(i+1) == '/')
                                {//주석인 경우 행 끝까지 주석처리
                                    tokens[indexToken] = new Token("//", cmdLine.substring(i+2));
                                    i = cmdLine.length();//행 분석 종료
                                    break;
                                }
                                else {tokens[indexToken] = new Token("Operator", "/");}
                                break;
                            case '+':
                                if(cmdLine.charAt(i+1) == '+')
                                {//++기호인 경우
                                    tokens[indexToken] = new Token("Increment", cmdLine.substring(i+2));
                                }
                                else {tokens[indexToken] = new Token("Operator", "+");}
                                break;
                            case '-':
                                if(cmdLine.charAt(i+1) == '-')
                                {//--기호인 경우
                                    tokens[indexToken] = new Token("Decrement", cmdLine.substring(i+2));
                                }
                                else {tokens[indexToken] = new Token("Operator", "-");}
                                break;
                            case '*':
                                {tokens[indexToken] = new Token("Operator", "*");}
                                break;
                            case '<':
                                if(cmdLine.charAt(i+1) == '=')
                                {//<=기호인 경우
                                    tokens[indexToken] = new Token("Operator", "<=");
                                    i++;
                                }
                                else {tokens[indexToken] = new Token("Operator", "<");}
                                break;
                            case '>':
                                if(cmdLine.charAt(i+1) == '=')
                                {//>=기호인 경우
                                    tokens[indexToken] = new Token("Operator", ">=");
                                    i++;
                                }
                                else {tokens[indexToken] = new Token("Operator", ">");}
                                break;
                            case '=':
                                if(cmdLine.charAt(i+1) == '=')
                                {//==기호인 경우
                                    tokens[indexToken] = new Token("Operator", "==");
                                    i++;
                                }
                                else {tokens[indexToken] = new Token("Operator", "=");}
                                break;
                            case '&':
                                if(cmdLine.charAt(i+1) == '&')
                                {//&&기호인 경우
                                    tokens[indexToken] = new Token("AND Operator", "&&");
                                    i++;
                                }
                                else {tokens[indexToken] = new Token("Operator", "&");}
                                break;
                            case '|':
                                if(cmdLine.charAt(i+1) == '|')
                                {// ||기호인 경우
                                    tokens[indexToken] = new Token("OR Operator", "||");
                                    i++;
                                }
                                break;
                            case '(': case ')': case '{': case '}':
                                tokens[indexToken] = new Token("Bracket", Character.toString(cmdLine.charAt(i)));
                                break;
                            default:
                                tokens[indexToken] = new Token("none", Character.toString(cmdLine.charAt(i)));
                        }

                        indexToken++;
                    }


                }
                for(int i=0; i<indexToken; i++)
                {
                    toToken = tokens[i];
                    System.out.println(toToken.toString());
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e);
            System.exit(1);
        }
        catch (IOException e)
        {
            System.out.println(e);
            System.exit(1);
        }
    }

    private static String concat(String set, String cmdLine, int index)
    {
        String return_str = "";
        do {
            return_str += cmdLine.charAt(index++);
        }
        while(set.indexOf(cmdLine.charAt(index)) >= 0 && index <= cmdLine.length());
        return return_str;
    }
}



class Token
{
    public String tokenType;
    public String value = "";

    public Token (String t, String v) {
        tokenType = t;
        value = v;
    }

    public String toString()
    {
        if(value == "VarType")
        {
            return tokenType;
        }
        else if(tokenType=="none")
        {
            return value;
        }
        else
        {
            return tokenType + "\t" + value;
        }
    }
}
