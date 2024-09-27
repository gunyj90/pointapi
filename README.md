## Point API 프로젝트 설명

수기로 포인트를 핸들링하는 API를 제공합니다.

회원을 기준으로 포인트를 적립, 적립취소, 사용, 사용취소 할 수 있습니다.

* 적립: 회원ID와 적립포인트를 받아 포인트가 수기로 적립됩니다.
 1회 적립가능 포인트는 회원정보에 저장됩니다. 회원정보수정에서 이를 변경할 수 있습니다.
 더해 최대소유할 수 있는 포인트를 제한할 수 있습니다.
 적립요청시 포인트 유효일수를 제공받습니다. 제공하지 않으면 1년으로 설정됩니다.
 성공적으로 적립되면 적립ID와 포인트를 결과값으로 제공합니다.


* 적립취소: 적립ID를 받아 적립한 내역을 취소합니다.
 적립상태가 ACCUMULATED가 아니면 (사용이력이 있으면) 취소할 수 없습니다.


* 사용: 회원ID와 사용포인트, 주문번호를 받아 포인트를 사용합니다.
 사용 포인트는 수기입력된 것으로 우선으로 하되 만료일이 짧은 순으로 사용합니다.
 적립한 포인트보다 많은 포인트를 사용할 수 없습니다.
 사용이 성공적으로 이루어지면 사용ID와 포인트를 결과값으로 제공합니다.


* 사용취소: 사용ID와 사용취소포인트를 받아 이를 취소합니다.
 사용취소포인트는 일부 혹은 전부가 될 수 있지만 사용포인트를 초과할 수 없습니다.
 적립포인트가 만료되면 같은 유효일수로 새로운 적립포인트를 생성합니다.

## 리소스경로
```
/src/main/resources/ERD.png
/src/main/resources/AWS구축 및 설계.png
```

## 실행방법

1. 프로젝트 설치 경로로 이동한다.

2. 명령어를 실행하여 jar파일을 생성한다.

```shell
./gradlew bootJar
```

3. 아래 명령어로 서버를 기동한다.

```shell
java -jar ./build/libs/pointapi-0.0.1-SNAPSHOT.jar
```
서버기동 후 H2콘솔 및 Swagger접속이 가능합니다.
* [H2콘솔로 접속하기](http://localhost:8080/h2-console)
* [Swagger로 접속하기](http://localhost:8080/api-test)

## 사용기술

* Java 21
* Gradle 8.8
* Spring boot 3.3.4
* Hibernate 6.5.3
* H2 2.2.224
* Junit 5
* Fixture monkey 1.0.26
  <br/>

## 프로젝트 설계
포인트 적립시 ACCUMULATION(A)을 생성합니다. 
```
[A1: 300포인트, 24/01/01 ~ 24/12/31, 적립상태]
```

적립을 취소하면 ACCUMULATION(A)의 상태 변경합니다.
```
[A1: 300포인트, 24/01/01 ~ 24/12/31, 취소상태]
```

포인트를 사용할때 USAGE(U)를 생성하고 사용할 수 있는 ACCUMULATION(A)을 조회해 금액을 차감해 나갑니다.
 차감할때 하나의 ACCUMULATION에 사용된 금액을 담은 ACCUMULATION_USAGE(AU)를 건별로 생성합니다.
```
[U1: 100포인트, 24/02/01, 사용상태]

[AU1: U1, A1, 100포인트, 사용상태]

[A1: 300포인트 -> 200포인트, 24/01/01 ~ 24/12/31, 적립상태->일부사용상태]
[A2: 400포인트, 24/01/01 ~ 24/1/31, 적립상태]
```

사용에 대해 취소할 때, USAGE->ACCUMULATION_USAGE->ACCUMULATION 순으로 조회해서 ACCUMULATION의 
 적립금액을 다시 증가시킵니다. 만료된 적립이 있을경우 ACCUMULATION_USAGE의 부분사용금액으로 새로운 ACCUMULATION을 
 생성합니다.
```
[U1: 100포인트, 24/02/01, 사용상태->취소상태]

[AU1: U1, A1, 100포인트, 사용상태->취소상태]

[A1: 200포인트->300포인트, 24/01/01 ~ 24/12/31, 일부사용상태->적립상태]
[A2: 400포인트, 24/01/01 ~ 24/1/31, 적립상태]
만료시) [A3: 100포인트, 25/01/01 ~ 25/12/31, 적립상태]
```

## 문제인식 및 해결

### 동시사용에 대한 데이터관리
포인트를 사용하면 요청한 포인트 만큼 적립포인트가 차감됩니다.
동시에 여러 요청에 의해 포인트가 갱신될때 읽는 시점과 쓰는 시점 중간에 다른 작업이 있어나면 데이터를 정합성이 깨집니다.
이는 Lock을 통해 데이터의 정합성을 유지할 수 있습니다.

> 비관적Lock: 데이터를 사용하는 동안 다른 트랜잭션이 해당 데이터에 접근하거나 수정하지 못하도록 합니다. 데이터에 대한 충돌이 발생할 가능성을 피하기 위해, 데이터에 접근할 때마다 잠금을 걸어 다른 트랜잭션이 접근하지 못하게 합니다.

> 낙관적Lock: 데이터 충돌이 드물게 발생할 것이라고 가정하고, 트랜잭션이 끝날 때까지 잠금을 사용하지 않습니다. 트랜잭션이 종료될 때 충돌 여부를 검사하여 충돌이 발생한 경우 트랜잭션을 롤백하고 다시 시도합니다.

비관적Lock이 프로그램의 성능을 저해할 수 있고 모든 트랜잭션의 순서를 보장하지 않아도 되는 수준임에 따라 낙관적Lock을 사용하였습니다.

JPA의 변경감지(dirty check)를 통해 포인트를 갱신하고
version을 통해 낙관적Lock을 구현하고 버전이 일치하지 않았을때 exception을 발생시킵니다.
ObjectOptimisticLockingFailureException이 발생하면 재시도합니다.

갱신할때 대상조회시 order by로 순서를 유지시켜 교착상태를 방지합니다.

