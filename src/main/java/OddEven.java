import java.util.Random;
import java.util.Scanner;

public class OddEven {

    private int betMarble, ranMarble;
    private int userMarble, comMarble;
    private boolean isMyTurn;
    private  String userId, userPw;
    private void clearVar(){
        this.userMarble = 10;
        this.comMarble = 10;
        this.isMyTurn = true;
        this.userId = null;
        this.userPw = null;
    }
    Scanner sc = new Scanner(System.in);
    Random random = new Random();
    GameDao dao = new GameDaoImpl();


    // 게임 시작
    public void start(){
        String tmpAnswer = null;
        GameDto udto = new GameDto();
        System.out.println("오징어 홀짝 게임");
        System.out.print("아이디가 있습니까? ( y / n ) : ");
        tmpAnswer = sc.next();
        while (true) {
            if (tmpAnswer.equalsIgnoreCase("y")){
                // 아이디, pw 입력 받고
                // DB에 있는지 확인
                // 없으면 다시 입력
                do{
                    System.out.println("로그인");
                    System.out.print("아이디 : ");
                    userId = sc.next();
                    System.out.print("패스워드 : ");
                    userPw = sc.next();
                    udto = dao.findIdPw(userId, userPw);
                }while (udto.getUserName() == null);


                // 게임 변수 초기화
                clearVar();

                // 로그인 후 자신의 아이디에 따라 구슬 갯수 동기화
                userMarble = udto.getUserBead();
                System.out.println(udto.getUserName() + "님 로그인");
                System.out.println("구슬 : " + userMarble);

                // 게임 실핼
                while (this.isGameOver()) {
                    this.betting();
//                  game.userTurn();
//                  game.comTurn();
                    this.checkAnswer();
                    this.turnChange();
                }

                System.out.println("구슬 : " + userMarble);
                // 게임 실행 후 데이터 저장 여부 물어본 뒤 y / n -> y면 저장
                System.out.print("저장하시겠습니까? ( y / n ) : ");
                tmpAnswer = sc.next();
                if(tmpAnswer.equalsIgnoreCase("y")){
                    dao.update(udto.getUserInd(), userMarble);
                }else if (userMarble <= 0){
                    dao.delete(udto.getUserInd());
                }
                tmpAnswer = "y";
            }else{
                // 신규 가입
                // id, pw, name DB에 입력
                // 로그인으로
                GameDto tdto = new GameDto();
                System.out.println("회원가입");
                System.out.print("아이디 : ");
                tdto.setUserId(sc.next());
                System.out.print("패스워드 : ");
                tdto.setUserPw(sc.next());
                System.out.print("이름 : ");
                tdto.setUserName(sc.next());
                dao.save(tdto);
                tmpAnswer = "y";
            }

        }
    }

    private void intro(){
        System.out.println("홀짝 게임");
        System.out.println("두 명의 참가자가 각각 10개의 구슬 보유");
        System.out.println("10개의 구슬을 다 잃을 시 사망");
        System.out.println("게임 시작");
    }
    private void betting(){
        random.setSeed(System.currentTimeMillis());
        while(true){
            if(isMyTurn) {
                try {
                    System.out.print("배팅할 구슬의 갯수 : ");
                    betMarble = sc.nextInt(); // 숫자 스캔 후 입력 받기
                    if (betMarble > userMarble) {
                        System.out.println("구슬 갯수 초과 ");
                    } else if (betMarble > comMarble) {
                        System.out.println("상대 구슬 갯수 모자람");
                    } else {
                        System.out.println("배팅된 구슬 갯수 : " + betMarble);
                        break;
                    }
                }catch (Exception e){
                    System.out.println("숫자만 입력하세요");
                    sc.nextLine(); //엔터키 값 처리(버퍼)
                }

            }else{
                betMarble = random.nextInt(Math.min(comMarble, userMarble)) + 1;
                break;
            }
        }
    } // public void betting

    private String comTurn(){
        ranMarble = random.nextInt(Math.min(comMarble, userMarble)) * random.nextInt(Math.min(comMarble, userMarble)) % 10 + 1;

        String isOdd = ranMarble % 2 == 0 ? "짝" : "홀";
        System.out.println("컴퓨터가 선택한 구슬 갯수 : " + isOdd);

        if(!isMyTurn){
            System.out.println("상대방 배팅 : " + betMarble);
            betMarble *= -1;
        }
        return isOdd;
    }

    private String userTurn(){
        String answer;
        while(true){
            if(isMyTurn) {
                System.out.print("홀 / 짝 예측( 홀 / 짝 ) : ");
            }else{
                System.out.print("문제 내기( 홀 / 짝 ) : ");
            }
            answer = sc.next();
            if(answer.contains("홀") || answer.contains("짝")){
                break;
            }
            System.out.println("입력 오류");
        }
        return answer;
    }

    private void checkAnswer(){
        String answer = userTurn();
        String isOdd = comTurn();

        if(answer.equals(isOdd)){
            System.out.println("\n정답");
            userMarble += betMarble;
            comMarble -= betMarble;
        }else {
            System.out.println("\n오답");
            userMarble -= betMarble;
            comMarble += betMarble;
        }
    }

    private void turnChange(){
        isMyTurn = !isMyTurn;
        System.out.println("\n");
        System.out.println("현재 구슬 갯수 : " + userMarble);
        System.out.println("상대 구슬 갯수 : " + comMarble);
    }

    private boolean isGameOver(){
        boolean isOver = true;
        if(userMarble <= 0){
            return false;
        } else if (userMarble >= 20 && comMarble <= 0) {
            System.out.println("이김");
            return false;
        }
        return isOver;
    }
}