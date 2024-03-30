# 미세먼지 경보 시스템   

## 🛠️ 사용 기술   
- Java 17
- SpringBoot 3.1.10
- MySQL
- vscode   

<br>

## 📊 데이터 설계   
![image](https://github.com/jisuyoun/particulate/assets/122525676/51a651c8-6d69-42d7-b60a-8b7f4c03a018)

- **tb_station_info**: 영업소가 있는 시들을 넣었습니다.
- **tb_inspection_info**: 각 영업소의 미세먼지, 초미세먼지 측정기의 점검 시기를 저장합니다.
- **tb_particulate_info**: 각 영업소의 미세먼지, 초미세먼지 농도를 저장합니다.
- **tb_alert_info**: 미세먼지 및 초미세먼지의 경보, 주의보 정보를 저장합니다.   

<br>

## 🔎 진행 과정   
스프링부트를 실행시키면 csv 파일을 열어 데이터를 DB에 넣는 작업이 진행되도록 하였습니다.

csv 파일은 /src/main/resources/csv 경로에 있는 csv 파일들을 인식하도록 하였습니다.  
<span style="color:gray;font-size:10pt;">csv 파일이 여러 개 있을 수 있다는 가정하에 코드를 작성하였습니다.</span>

csv 파일을 열어 2시간 이상 일정 농도 이상 진행되는 미세먼지 및 초미세먼지에 의한 등급을 console에 경보 또는 주의보가 지속되고 있는 시간과 영업소가 출력되도록 하였습니다.

![Animation](https://github.com/jisuyoun/particulate/assets/122525676/a8d9b592-41b5-491b-82f7-9e54125df66d)


<span style="color:red">**만약 DB에 중복되는 도시, 영업소, 일시가 있다면 중복처리되어 DB에 저장되지 않도록 하였습니다.**</span>

![Animation](https://github.com/jisuyoun/particulate/assets/122525676/9633668d-e344-4ab5-bf3e-134768c19dd3)


실행이 된 후 console에 출력되었던 log들은 모두 **src/main/resources/logs/{실행날짜}** 경로에 **errorLog{실행날짜}.log**로 저장되도록 코드를 잘성하였습니다.   

<br>   

## ✨ 주요 메소드   
<details>
<summary>1. 측정기 점검일자</summary>
<div markdown="1">

```java 
if (modifiableList.get(3).isEmpty()) {
    // 측정 농도 중 미세먼지 측정값만 없을 경우 미세먼지 측정기 점검 날로 가정한다.
    modifiableList.set(3, "0");

    inspectionType = "part";

}

if (modifiableList.size() < 5 || modifiableList.get(4).isEmpty()) {
    // 측정 농도 중 초미세먼지 측정값만 없을 경우 초미세먼지 측정기 점검 날로 가정한다.
    inspectionType = "fine";

    if (modifiableList.size() < 5) {
        modifiableList.add("0");

    } else {
        modifiableList.set(4, "0");

    }
}

csvList = modifiableList; // 점검일에 0을 넣은 리스트로 변경
```
- csv 파일에서 농도 부분이 모두 빈칸일 경우 modifiableList의 사이즈는 3이 되므로, 0 값을 두 개 추가하였습니다.   
- csv 파일에서 미세먼지 농도만 빈칸일 경우 미세먼지가 들어가는 인덱스인 3에 0 값을 넣도록 하였습니다.   
- csv 파일에서 초미세먼지 농도만 빈칸일 경우 0 값이 추가되도록 하였습니다.   

```java
if (!"".equals(inspectionType)) {
    // 점검 정보를 insert
    switch (inspectionType) {
        case "dual": 
            inspectionType = "모든 측정기";
            break;

        case "part":
            inspectionType = "미세먼지 측정기";
            break;
        
        default:
            inspectionType = "초미세먼지 측정기";
            break;
    }

    modifiableList.add(inspectionType);

    partMapper.insertInspection(modifiableList);
}
```
- 위 메소드를 통해 나타난 점검 여부를 가지고 TB_INSPECTION_INFO 테이블에 정보를 insert 합니다.   
</div>
</details>

<details>
<summary>2. 경보 또는 주의보 알림</summary>
<div markdown="1">

```java
int partValue = Integer.parseInt(csvList.get(3));

if (partValue >= 300) {
    // 미세먼지 경보일 경우, 주의보는 cnt 0으로 바꿔준다.
    grade2Cnt++;
    grade4Cnt = 0;
} else if (partValue < 300 && partValue >= 150) {
    // 미세먼지 주의보일 경우
    grade2Cnt = 0;
    grade4Cnt++;
} else {
    // 미세먼지 경보도 주의보도 아닐 경우
    grade2Cnt = 0;
    grade4Cnt = 0;
}

int fineValue = Integer.parseInt(csvList.get(4));

if (fineValue >= 150) {
    // 초미세먼지 경보일 경우, 주의보는 cnt 0으로 만들어준다.
    grade1Cnt++;
    grade3Cnt = 0;
} else if (fineValue < 150 && fineValue >= 75) {
    // 초미세먼지 주의보일 경우
    grade1Cnt = 0;
    grade3Cnt++;
} else {
    // 초미세먼지 경보도 주의보도 아닐 경우
    grade1Cnt = 0;
    grade3Cnt = 0;
}

gradeList.add(grade1Cnt);
gradeList.add(grade2Cnt);
gradeList.add(grade3Cnt);
gradeList.add(grade4Cnt);

// 미세먼지의 등급을 알아본다.
alertGrade(gradeList, csvList);
```
- 일정 농도 이상의 미세먼지 및 초미세먼지가 되었을 경우 각 등급의 카운트를 올려줍니다.   

```java 
    private List<String> alertGrade(List<Integer> gradeList, List<String> csvList) {
                
        String grade = "";

        int grade1Cnt = gradeList.get(0);
        int grade2Cnt = gradeList.get(1);
        int grade3Cnt = gradeList.get(2);
        int grade4Cnt = gradeList.get(3);

        if (grade1Cnt >= 2) {
            grade = "1";
        } else if (grade2Cnt >= 2) {
            grade = "2";
        } else if (grade3Cnt >= 2) {
            grade = "3";
        } else if (grade4Cnt >= 2) {
            grade = "4";
        } else {
            grade = "";
        }

        if (grade != "") {

            log.info(csvList.get(0) + "시 " + csvList.get(1) + " 영업소는 현재 대기 등급 " + grade + " 입니다.");

            try {
                
                csvList.add(grade); // 리스트에 등급을 추가로 넣어준다.

                // 미세먼지 등급을 기록한다.
                partMapper.insertAlertInfo(csvList);

            } catch (Exception e) {
                log.error("에러 => 미세먼지 경보 중 에러 발생 " + e);
                e.printStackTrace();
            }
        }
        return csvList;
    }
```   
그 후, alertGrade 메소드를 통해 각 등급의 카운트를 계산하여 카운트가 2이상인 경우를 콘솔을 통해 사용자에게 알려줍니다.
</div>
</details>

<details>
<summary>3. 영업소별 농도 저장</summary>
<div markdown="1">

```java
    // 각 측정소 별 미세먼지와 초미세먼지 농도를 삽입한다.
    partMapper.insertPartInfo(csvList);
```
</div>
</details>

<br>

## ✍️개선할 점   
- 웹훅을 적용하기 위해 시도해보았으나 실패했다.
- 웹훅을 적용하기위해 OKHttp를 적용하여 코드를 작성해보았으나, 아래와 같은 오류가 발생하는 문제가 생겼다.. 해당 문제를 해결하기 구글링을 해보았으나 SSL 통신 중 생긴 문제라는 것을 알게 되었으나, 해결 방법은 찾지 못 해 결국 웹훅은 구현하지 못 했다.

```
03-30 23:23:45 [INFO ] o.a.c.h.Http11Processor.log - Error parsing HTTP request header
 Note: further occurrences of HTTP request parsing errors will be logged at DEBUG level.
java.lang.IllegalArgumentException: Invalid character found in method name [0x160x030x030x010x920x010x000x010x8e0x030x030x880xb50x8b3a0xea!0xeaL}0xa50xfc0xbf`0xa00xda+0xc80x1b0xeb&? ]. HTTP method names must be tokens
```
- 웹훅에 대한 공부를 더 하고 적용해보도록 해야겠다.   