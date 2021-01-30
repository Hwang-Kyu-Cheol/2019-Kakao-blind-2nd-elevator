# 2019 카카오 블라인드 공채 2차 오프라인 코딩 테스트 문제 풀이

## 1. 문제 설명

문제와 Elevator 서버 코드: https://github.com/kakao-recruit/2019-blind-2nd-elevator

문제와 해설: https://tech.kakao.com/2018/10/23/kakao-blind-recruitment-round-2/

## 2. 프로젝트 구성
```
    .src/com/company
    ├── api    .
               ├── Api.class
               
    ├── domain .
               ├── Call.class
               ├── Command.class
               ├── Direction.enum
               ├── Elevator.class
               ├── Instruction.enum
               ├── Status.enum
               
    ├── json   .
               ├── request   .
                             ├── Action.class
              
               ├── response  .
                             ├── OnCalls.class
                             ├── Start.class
                             
    ├── Main.class              
```

### Main.class

시작을 위한 메인 클래스
``` java
api.start()
while (true) {
  api.on_calls()
  if (is_end())
    break;
  
  for (Elevator elevator : elevatorList) {
    case STOPPED:
      ...
    case OPENED:
      ...
      
    action.add(command);
  }
  api.action(action)
}
```

### api / Api.class

서버와 통신을 위한 클래스

``` java
Api api = new Api(user_key, problem_id, number_of_elevators) # new Api(유저 키, 문제 번호, 사용할 엘리베이터 수)

api.start();

api.on_calls();

api.action(String actionJson);
```
start, on_calls, action 메서드를 제공한다.

### domain

엘리베이터 동작에 필수적인 클래스들을 모아놓은 디렉토리

`Elevator.class` : 엘리베이터 클래스, 클래스 내부의 direction 필드는 해당 엘리베이터가 가는 방향(`Direction.enum` : GoingUp, GoingDown)  
`Call.class` : 요청 클래스, 클래스 내부의 direction 필드는 해당 요청이 가고자 하는 방향(`Direction.enum` : GoingUp, GoingDown)  
`Command.class` : 엘리베이터 제어 명령 클래스  

`Status.enum` : 엘리베이터 상태, STOPPED / OPENED / UPWARD / DOWNWARD  
`Instruction.enum` : 명령어 모음, STOP / UP / DOWN / OPEN / CLOSE / ENTER / EXIT

### json

#### json / request
`Action.class` : 서버에 요청할 때 보내는 Action. 뒤에서도 언급할 Gson 라이브러리를 사용해서, Action 객체를 Json 형태로 변환한다.

#### json / response
`Start.class` : 서버에서 응답한 Json 형식을 담을 클래스(Gson으로 변환)
`OnCalls.class` : (위와 동일)

## 3. 아이디어

### Gson 라이브러리 사용

JSON -> 객체, 객체 -> JSON 을 쉽게 도와주는 Gson 라이브러리를 사용하였습니다. 
``` java
Gson gson = new Gson();

Start start = gson.fromJson(api.start(), Start.class);
OnCalls onCalls = gson.fromJson(api.on_calls(), OnCalls.class);
api.action(gson.toJson(action))
  ...
```
위와 같이 사용할 수 있습니다.

### Look 알고리즘을 사용

엘리베이터의 진행 방향보다 앞에 요청이 있다면, 진행 방향을 유지하고 앞에 요청이 없다면 진행 방향을 바꾸는 방식으로 구현하였습니다.  
예를 들어, 엘리베이터가 올라 가고 있고 3층에 있다고 가정하겠습니다.  
만약 1층과 5층에서 각각 요청이 있다면, 1층의 요청이 먼저들어왔더라도 진행 방향 보다 앞인 5층의 요청을 처리하러 올라가는 방식입니다.  
쉽게 말해서, 아래에서 부터 위까지 쓸어올라가고 다시 위에서 아래까지 쓸어내려가는 방식 입니다.

## 4. 결과
> Start Params
```java
String user_key = "tester2";

		int problem_id = 0; 
    //  0: 어피치 맨션
    //  1: 제이지 빌딩
    //  2: 라이언 타워
    
		int number_of_elevators = 4;
    int elevator_maxSize = 8;
```
 **1. 어피치 맨션 Total Timestamp: 25**

    AveWait: 9.333333, AveTravel: 7.000000, AveTotal: 16.333333, LastTs: 25, Status: OK

  **2. 제이지 빌딩 Total Timestamp 635**

    AveWait: 62.175000, AveTravel: 25.590000, AveTotal: 87.765000, LastTs: 635, Status: OK

 **3. 라이언 타워 Total Timestamp 1895**

    AveWait: 26.108000, AveTravel: 17.928000, AveTotal: 44.036000, LastTs: 1895, Status: OK
    
## 4. 문제점

### (1) 각각 엘리베이터 제어 분리 X

엘리베이터 4대를 동작하였지만, 4대가 모두 같은 방식으로 움직이는 문제점이 있습니다.  
예를 들어, 1번 2번 엘리베이터가 7층에 있고, 방향은 올라가고 있습니다. 요청은 각각 5층가 9층에 있다고 가정하겠습니다.  
이 때, 1번은 5층으로 가고 2번은 9층으로 가는 모습이 효율적인 엘리베이터 제어 입니다.  
하지만, 제가 구현한 엘리베이터는 모두 9층으로 올라갑니다.  
1번 엘리베이터는 승객을 태우지만, 2번 엘리베이터는 승객을 태우지 못합니다. 못하는걸 알면서도 9층으로 올라갑니다.

이 부분을 해결하려면 각각의 엘리베이터 제어를 분리해야한다고 생각합니다. 좀 더 고민해봐야 할 것 같습니다.

### (2) 상황에 맞는 엘리베이터 제어 X

문제 2번의 경우, 특별한 상황이 주어집니다.
하지만, 이에 맞게 효율적인 엘리베이터 제어가 되지 않습니다.  

이 부분을 해결하려면 위와 같이 엘리베이터 제어를 분리해야한다고 생각합니다.

## 5. 느낀점

엘리베이터 제어 방식을 정하고, 구현함에 있어서는 큰 어려움이 없었습니다.  
하지만, 이 후 **모듈화** 하는 과정이 어려웠던 것 같습니다.

처음에는 구현을 위한 구현을 하였습니다. 일단, 동작하는 엘리베이터를 구현하는 것이 목적이었습니다.  
그 후에, "객체지향스럽게" 바꾸는 과정을 진행하였습니다. Spring을 배우면서 주워 들었던 지식들을 활용하고, 이것저것 서툴게 리팩토링한 코드가 이것입니다.  
정말 부족함을 많이 느꼈습니다. '설계란 것이 이런 것이구나'를 몸으로 체험했습니다.

구현에 있어서 큰 문제를 겪지 않은점은 본인에게 칭찬하고 싶습니다. 아마도, 알고리즘 문제를 풀면서 쌓았던 실력이라고 생각합니다.  
하지만, 무언가 벽에 부딪힌 느낌이 들었습니다. 차근차근 공부하며, 이 벽을 허물고 싶습니다.

**황규철**(Hwang-Kyu-Cheol)
