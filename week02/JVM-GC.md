# JVM GC

```java

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class GCLogAnalysis {
    private static Random random = new Random();
    public static void main(String[] args) {
        // 当前毫秒时间戳
        long startMillis = System.currentTimeMillis();
        // 持续运行毫秒数; 可根据需要进行修改
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        // 结束时间戳
        long endMillis = startMillis + timeoutMillis;
        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");
        // 缓存一部分对象; 进入老年代
        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];
        // 在此时间范围内,持续循环
        while (System.currentTimeMillis() < endMillis) {
            // 生成垃圾对象
            Object garbage = generateGarbage(100*1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束!共生成对象次数:" + counter.longValue());
    }

    // 生成对象
    private static Object generateGarbage(int max) {
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while (builder.length() < randomSize) {
                    builder.append(randomString);
                    builder.append(max);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}

```

java命令：java -XX:+PrintGCDetails GCLogAnalysis

```
正在执行...
[GC (Allocation Failure) [PSYoungGen: 65024K->10740K(75776K)] 65024K->19895K(249344K), 0.0043612 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 75696K->10751K(140800K)] 84851K->39373K(314368K), 0.0053986 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 140799K->10750K(140800K)] 169421K->78861K(314368K), 0.0140810 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 140798K->10743K(270848K)] 208909K->125820K(444416K), 0.0126597 secs] [Times: user=0.05 sys=0.11, real=0.01 secs]
[Full GC (Ergonomics) [PSYoungGen: 10743K->0K(270848K)] [ParOldGen: 115077K->114777K(261120K)] 125820K->114777K(531968K), [Metaspace: 2704K->2704K(1056768K)], 0.0239549 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 260096K->10736K(270848K)] 374873K->198838K(531968K), 0.0153152 secs] [Times: user=0.05 sys=0.11, real=0.02 secs]
[Full GC (Ergonomics) [PSYoungGen: 10736K->0K(270848K)] [ParOldGen: 188102K->175460K(388096K)] 198838K->175460K(658944K), [Metaspace: 2704K->2704K(1056768K)], 0.0291663 secs] [Times: user=0.30 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 260039K->77057K(472064K)] 435500K->252517K(860160K), 0.0164107 secs] [Times: user=0.03 sys=0.13, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 471297K->102896K(497152K)] 646757K->341315K(885248K), 0.0344128 secs] [Times: user=0.16 sys=0.16, real=0.04 secs]
[GC (Allocation Failure) [PSYoungGen: 497136K->152564K(723456K)] 735555K->423925K(1111552K), 0.0376010 secs] [Times: user=0.11 sys=0.24, real=0.04 secs]
[GC (Allocation Failure) [PSYoungGen: 723444K->193528K(764416K)] 994805K->525936K(1152512K), 0.0449319 secs] [Times: user=0.20 sys=0.26, real=0.04 secs]
[Full GC (Ergonomics) [PSYoungGen: 193528K->0K(764416K)] [ParOldGen: 332408K->335816K(580608K)] 525936K->335816K(1345024K), [Metaspace: 2704K->2704K(1056768K)], 0.0671353 secs] [Times: user=0.58 sys=0.00, real=0.07 secs]
[GC (Allocation Failure) [PSYoungGen: 570880K->157621K(1025536K)] 906696K->493438K(1606144K), 0.0302168 secs] [Times: user=0.14 sys=0.17, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 932277K->256505K(1031168K)] 1268094K->610200K(1611776K), 0.0465387 secs] [Times: user=0.39 sys=0.08, real=0.05 secs]
执行结束!共生成对象次数:13764
Heap
 PSYoungGen      total 1031168K, used 287542K [0x000000076b900000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 774656K, 4% used [0x000000076b900000,0x000000076d74f568,0x000000079ad80000)
  from space 256512K, 99% used [0x000000079ad80000,0x00000007aa7fe500,0x00000007aa800000)
  to   space 303616K, 0% used [0x00000007ad780000,0x00000007ad780000,0x00000007c0000000)
 ParOldGen       total 580608K, used 353695K [0x00000006c2a00000, 0x00000006e6100000, 0x000000076b900000)
  object space 580608K, 60% used [0x00000006c2a00000,0x00000006d8367c58,0x00000006e6100000)
 Metaspace       used 2710K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 291K, capacity 386K, committed 512K, reserved 1048576K
```

```
未指定xmx、xms时，堆内存的容量出现递增浮动？？？
因为未指定xms，默认xmx是16g/4=4g，JVM初始化的堆内存容量随着堆内存空间的不断使用，在不断的增大，会比xmx=xms时触发更多次Minor GC。
```

默认：物理内存>1g时，xmx和xms是物理内存的1/4；物理内存<1g时，xmx和xms是物理内存的1/2

java命令：java -XX:+PrintGCDetails -Xmx1g -Xms1g GCLogAnalysis

```
正在执行...
[GC (Allocation Failure) [PSYoungGen: 262144K->43509K(305664K)] 262144K->79691K(1005056K), 0.0142068 secs] [Times: user=0.11 sys=0.05, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 305653K->43510K(305664K)] 341835K->154825K(1005056K), 0.0227208 secs] [Times: user=0.09 sys=0.06, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 305654K->43517K(305664K)] 416969K->225361K(1005056K), 0.0166805 secs] [Times: user=0.00 sys=0.16, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 305661K->43510K(305664K)] 487505K->299768K(1005056K), 0.0228943 secs] [Times: user=0.25 sys=0.06, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 305654K->43518K(305664K)] 561912K->369974K(1005056K), 0.0309540 secs] [Times: user=0.20 sys=0.09, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 305662K->43516K(160256K)] 632118K->444448K(859648K), 0.0267194 secs] [Times: user=0.11 sys=0.19, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 160252K->69549K(232960K)] 561184K->478425K(932352K), 0.0122962 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 186285K->95905K(232960K)] 595161K->514440K(932352K), 0.0198661 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 212641K->105500K(232960K)] 631176K->540888K(932352K), 0.0217278 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 222236K->75635K(232960K)] 657624K->573460K(932352K), 0.0283728 secs] [Times: user=0.23 sys=0.08, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 192371K->39616K(232960K)] 690196K->605063K(932352K), 0.0228078 secs] [Times: user=0.22 sys=0.08, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 156352K->42341K(232960K)] 721799K->642259K(932352K), 0.0143493 secs] [Times: user=0.13 sys=0.03, real=0.02 secs]
[Full GC (Ergonomics) [PSYoungGen: 42341K->0K(232960K)] [ParOldGen: 599918K->333382K(699392K)] 642259K->333382K(932352K), [Metaspace: 2704K->2704K(1056768K)], 0.0771049 secs] [Times: user=0.69 sys=0.02, real=0.08 secs]
[GC (Allocation Failure) [PSYoungGen: 116475K->39692K(232960K)] 449858K->373075K(932352K), 0.0073609 secs] [Times: user=0.16 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 156338K->41769K(232960K)] 489721K->411904K(932352K), 0.0113989 secs] [Times: user=0.16 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 158505K->41876K(232960K)] 528640K->449189K(932352K), 0.0143008 secs] [Times: user=0.16 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 158612K->38313K(232960K)] 565925K->483062K(932352K), 0.0132639 secs] [Times: user=0.16 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 155049K->40569K(232960K)] 599798K->520715K(932352K), 0.0147952 secs] [Times: user=0.14 sys=0.00, real=0.02 secs]
[GC (Allocation Failure) [PSYoungGen: 157230K->41279K(232960K)] 637376K->558669K(932352K), 0.0134189 secs] [Times: user=0.14 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 158015K->39337K(232960K)] 675405K->595089K(932352K), 0.0172739 secs] [Times: user=0.17 sys=0.00, real=0.02 secs]
执行结束!共生成对象次数:11795
Heap
 PSYoungGen      total 232960K, used 65824K [0x00000000eab00000, 0x0000000100000000, 0x0000000100000000)
  eden space 116736K, 22% used [0x00000000eab00000,0x00000000ec4ddd38,0x00000000f1d00000)
  from space 116224K, 33% used [0x00000000f1d00000,0x00000000f436a670,0x00000000f8e80000)
  to   space 116224K, 0% used [0x00000000f8e80000,0x00000000f8e80000,0x0000000100000000)
 ParOldGen       total 699392K, used 555752K [0x00000000c0000000, 0x00000000eab00000, 0x00000000eab00000)
  object space 699392K, 79% used [0x00000000c0000000,0x00000000e1eba060,0x00000000eab00000)
 Metaspace       used 2710K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 291K, capacity 386K, committed 512K, reserved 1048576K
```

java命令-打印gc时间戳：java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx1g -Xms1g GCLogAnalysis

java命令-指定日志文件：java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gcLog -Xmx1g -Xms1g GCLogAnalysis

### 日志解析

[GC (Allocation Failure) [PSYoungGen: 262144K->43509K(305664K)] 262144K->79691K(1005056K), 0.0142068 secs] [Times: user=0.11 sys=0.05, real=0.02 secs]

- [GC (Allocation Failure) [PSYoungGen: 262144K->43509K(305664K)] 262144K->79691K(1005056K), 0.0142068 secs] ：堆内存的变化情况
  - 	GC (Allocation Failure) ：GC发生的原因（分配内存失败）
  - 	[PSYoungGen: 262144K->43509K(305664K)] 262144K->79691K(1005056K), 0.0142068 secs：本次gc执行时间为0.0142秒 ，young区使用量从262144K压缩到了43509K，当前~~最大~~容量为305664K；堆内存使用量从262144K到79691K，当前~~最大~~堆内存容量为1005056K

- [Times: user=0.11 sys=0.05, real=0.02 secs]：CPU使用的情况

  [Full GC (Ergonomics) [PSYoungGen: 42341K->0K(232960K)] [ParOldGen: 599918K->333382K(699392K)] 642259K->333382K(932352K), [Metaspace: 2704K->2704K(1056768K)], 0.0771049 secs] [Times: user=0.69 sys=0.02, real=0.08 secs]

```
问题：Full GC时，会把young区清空，步骤是不管分代年龄全都放入old区，再进行老年代的gc么？
```

java命令-指定serialGC（串行GC）：java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx1g -Xms1g -XX:+UseSerialGC GCLogAnalysis

```
正在执行...
2021-08-15T00:32:53.425+0800: [GC (Allocation Failure) 2021-08-15T00:32:53.425+0800: [DefNew: 279616K->34944K(314560K), 0.0330163 secs] 279616K->86245K(1013632K), 0.0336048 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2021-08-15T00:32:53.503+0800: [GC (Allocation Failure) 2021-08-15T00:32:53.503+0800: [DefNew: 314560K->34943K(314560K), 0.0380170 secs] 365861K->163705K(1013632K), 0.0384177 secs] [Times: user=0.02 sys=0.01, real=0.04 secs]
2021-08-15T00:32:53.581+0800: [GC (Allocation Failure) 2021-08-15T00:32:53.582+0800: [DefNew: 314559K->34943K(314560K), 0.0359310 secs] 443321K->246472K(1013632K), 0.0365939 secs] [Times: user=0.02 sys=0.02, real=0.04 secs]
2021-08-15T00:32:53.653+0800: [GC (Allocation Failure) 2021-08-15T00:32:53.653+0800: [DefNew: 314559K->34943K(314560K), 0.0368070 secs] 526088K->322591K(1013632K), 0.0372616 secs] [Times: user=0.00 sys=0.05, real=0.04 secs]
2021-08-15T00:32:53.729+0800: [GC (Allocation Failure) 2021-08-15T00:32:53.729+0800: [DefNew: 314559K->34943K(314560K), 0.0378670 secs] 602207K->402343K(1013632K), 0.0382985 secs] [Times: user=0.03 sys=0.02, real=0.04 secs]
2021-08-15T00:32:53.803+0800: [GC (Allocation Failure) 2021-08-15T00:32:53.803+0800: [DefNew: 314508K->34943K(314560K), 0.0333884 secs] 681908K->477546K(1013632K), 0.0337585 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2021-08-15T00:32:53.875+0800: [GC (Allocation Failure) 2021-08-15T00:32:53.875+0800: [DefNew: 314559K->34943K(314560K), 0.0355969 secs] 757162K->558062K(1013632K), 0.0361612 secs] [Times: user=0.00 sys=0.03, real=0.04 secs]
2021-08-15T00:32:53.950+0800: [GC (Allocation Failure) 2021-08-15T00:32:53.951+0800: [DefNew: 314504K->34943K(314560K), 0.0359273 secs] 837623K->640893K(1013632K), 0.0363163 secs] [Times: user=0.02 sys=0.03, real=0.04 secs]
2021-08-15T00:32:54.029+0800: [GC (Allocation Failure) 2021-08-15T00:32:54.030+0800: [DefNew: 314145K->314145K(314560K), 0.0003183 secs]2021-08-15T00:32:54.030+0800: [Tenured: 605949K->383373K(699072K), 0.0606489 secs] 920094K->383373K(1013632K), [Metaspace: 2704K->2704K(1056768K)], 0.0617758 secs] [Times: user=0.06 sys=0.00, real=0.06 secs]
2021-08-15T00:32:54.128+0800: [GC (Allocation Failure) 2021-08-15T00:32:54.129+0800: [DefNew: 279616K->34944K(314560K), 0.0151850 secs] 662989K->467341K(1013632K), 0.0157969 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2021-08-15T00:32:54.185+0800: [GC (Allocation Failure) 2021-08-15T00:32:54.186+0800: [DefNew: 313960K->34944K(314560K), 0.0188548 secs] 746358K->540049K(1013632K), 0.0192834 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2021-08-15T00:32:54.246+0800: [GC (Allocation Failure) 2021-08-15T00:32:54.247+0800: [DefNew: 314560K->34943K(314560K), 0.0207525 secs] 819665K->621871K(1013632K), 0.0213541 secs] [Times: user=0.03 sys=0.00, real=0.02 secs]
2021-08-15T00:32:54.313+0800: [GC (Allocation Failure) 2021-08-15T00:32:54.313+0800: [DefNew: 314559K->34944K(314560K), 0.0286240 secs] 901487K->691721K(1013632K), 0.0292253 secs] [Times: user=0.02 sys=0.00, real=0.03 secs]
执行结束!共生成对象次数:13643
Heap
 def new generation   total 314560K, used 46126K [0x00000000c0000000, 0x00000000d5550000, 0x00000000d5550000)
  eden space 279616K,   3% used [0x00000000c0000000, 0x00000000c0aebb80, 0x00000000d1110000)
  from space 34944K, 100% used [0x00000000d1110000, 0x00000000d3330000, 0x00000000d3330000)
  to   space 34944K,   0% used [0x00000000d3330000, 0x00000000d3330000, 0x00000000d5550000)
 tenured generation   total 699072K, used 656777K [0x00000000d5550000, 0x0000000100000000, 0x0000000100000000)
   the space 699072K,  93% used [0x00000000d5550000, 0x00000000fd6b2428, 0x00000000fd6b2600, 0x0000000100000000)
 Metaspace       used 2710K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 291K, capacity 386K, committed 512K, reserved 1048576K
```

```
SerialGC（串行化GC）Minor GC和Major GC花费的时间是差不多的，DefNew是垃圾回收器的名字，堆内存最大容量基本不变
```

java命令-指定CMS GC：java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx1g -Xms1g -XX:+UseConcMarkSweepGC GCLogAnalysis

```
正在执行...
2021-08-15T00:53:04.939+0800: [GC (Allocation Failure) 2021-08-15T00:53:04.939+0800: [ParNew: 279616K->34944K(314560K), 0.0133093 secs] 279616K->84227K(1013632K), 0.0139827 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2021-08-15T00:53:05.001+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.001+0800: [ParNew: 314560K->34942K(314560K), 0.0176623 secs] 363843K->161095K(1013632K), 0.0181658 secs] [Times: user=0.17 sys=0.06, real=0.02 secs]
2021-08-15T00:53:05.058+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.058+0800: [ParNew: 314558K->34944K(314560K), 0.0355896 secs] 440711K->242502K(1013632K), 0.0360051 secs] [Times: user=0.31 sys=0.00, real=0.04 secs]
2021-08-15T00:53:05.132+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.132+0800: [ParNew: 314296K->34944K(314560K), 0.0373025 secs] 521855K->325502K(1013632K), 0.0376793 secs] [Times: user=0.28 sys=0.03, real=0.04 secs]
2021-08-15T00:53:05.204+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.204+0800: [ParNew: 314560K->34944K(314560K), 0.0343443 secs] 605118K->405431K(1013632K), 0.0347432 secs] [Times: user=0.30 sys=0.02, real=0.04 secs]
2021-08-15T00:53:05.239+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 370487K(699072K)] 411153K(1013632K), 0.0007259 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T00:53:05.240+0800: [CMS-concurrent-mark-start]
2021-08-15T00:53:05.243+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T00:53:05.243+0800: [CMS-concurrent-preclean-start]
2021-08-15T00:53:05.243+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T00:53:05.244+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T00:53:05.280+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.281+0800: [ParNew: 314560K->34943K(314560K), 0.0385357 secs] 685047K->486584K(1013632K), 0.0389834 secs] [Times: user=0.45 sys=0.01, real=0.04 secs]
2021-08-15T00:53:05.357+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.357+0800: [ParNew: 314559K->34943K(314560K), 0.0355627 secs] 766200K->567924K(1013632K), 0.0359529 secs] [Times: user=0.42 sys=0.05, real=0.04 secs]
2021-08-15T00:53:05.433+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.433+0800: [ParNew: 314559K->34942K(314560K), 0.0383421 secs] 847540K->652924K(1013632K), 0.0388560 secs] [Times: user=0.45 sys=0.02, real=0.04 secs]
2021-08-15T00:53:05.509+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.509+0800: [ParNew: 314558K->314558K(314560K), 0.0002168 secs]2021-08-15T00:53:05.509+0800: [CMS2021-08-15T00:53:05.510+0800: [CMS-concurrent-abortable-preclean: 0.006/0.265 secs] [Times: user=1.45 sys=0.08, real=0.27 secs]
 (concurrent mode failure): 617981K->338923K(699072K), 0.0527576 secs] 932540K->338923K(1013632K), [Metaspace: 2704K->2704K(1056768K)], 0.0534259 secs] [Times: user=0.05 sys=0.00, real=0.05 secs]
2021-08-15T00:53:05.595+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.595+0800: [ParNew: 279616K->34942K(314560K), 0.0152565 secs] 618539K->431862K(1013632K), 0.0157577 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2021-08-15T00:53:05.611+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 396920K(699072K)] 437533K(1013632K), 0.0007345 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T00:53:05.612+0800: [CMS-concurrent-mark-start]
2021-08-15T00:53:05.615+0800: [CMS-concurrent-mark: 0.003/0.003 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T00:53:05.616+0800: [CMS-concurrent-preclean-start]
2021-08-15T00:53:05.618+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T00:53:05.618+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T00:53:05.660+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.661+0800: [ParNew: 314558K->34942K(314560K), 0.0208993 secs] 711478K->510956K(1013632K), 0.0213195 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
2021-08-15T00:53:05.721+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.721+0800: [ParNew: 314558K->34942K(314560K), 0.0198918 secs] 790572K->584100K(1013632K), 0.0202961 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
2021-08-15T00:53:05.779+0800: [GC (Allocation Failure) 2021-08-15T00:53:05.779+0800: [ParNew: 314558K->34943K(314560K), 0.0233868 secs] 863716K->663642K(1013632K), 0.0238265 secs] [Times: user=0.31 sys=0.00, real=0.02 secs]
执行结束!共生成对象次数:14773
Heap
 par new generation   total 314560K, used 287576K [0x00000000c0000000, 0x00000000d5550000, 0x00000000d5550000)
  eden space 279616K,  90% used [0x00000000c0000000, 0x00000000cf6b6308, 0x00000000d1110000)
  from space 34944K,  99% used [0x00000000d1110000, 0x00000000d332fec0, 0x00000000d3330000)
  to   space 34944K,   0% used [0x00000000d3330000, 0x00000000d3330000, 0x00000000d5550000)
 concurrent mark-sweep generation total 699072K, used 628698K [0x00000000d5550000, 0x0000000100000000, 0x0000000100000000)
 Metaspace       used 2710K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 291K, capacity 386K, committed 512K, reserved 1048576K
```

### 日志分析

```
2021-08-15T00:53:04.939+0800: [GC (Allocation Failure) 2021-08-15T00:53:04.939+0800: [ParNew: 279616K->34944K(314560K), 0.0133093 secs] 279616K->84227K(1013632K), 0.0139827 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
```

Youny区的GC和Parallel GC一样

```
2021-08-15T00:53:05.239+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 370487K(699072K)] 411153K(1013632K), 0.0007259 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T00:53:05.240+0800: [CMS-concurrent-mark-start]
2021-08-15T00:53:05.243+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T00:53:05.243+0800: [CMS-concurrent-preclean-start]
2021-08-15T00:53:05.243+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T00:53:05.244+0800: [CMS-concurrent-abortable-preclean-start]
...
2021-08-15T01:12:23.001+0800: [GC (Allocation Failure) 2021-08-15T01:12:23.001+0800: [ParNew: 314559K->314559K(314560K), 0.0002155 secs]2021-08-15T01:12:23.001+0800: [CMS2021-08-15T01:12:23.002+0800: [CMS-concurrent-abortable-preclean: 0.006/0.269 secs] [Times: user=1.06 sys=0.08, real=0.27 secs]
 (concurrent mode failure): 617630K->339654K(699072K), 0.0523017 secs] 932189K->339654K(1013632K), [Metaspace: 2704K->2704K(1056768K)], 0.0533335 secs] [Times: user=0.05 sys=0.00, real=0.05 secs]
```

- CMS Initial Mark：CMS初始化标记，标记GC Roots，old区大小：370487K(old区容量：699072K)，堆内存大小：411153K(堆内存容量：1013632K)，用时0.0007259 secs很短
- CMS-concurrent-mark: 0.002/0.002 secs：CMS并发标记对象0.002 secs
- CMS-concurrent-abortable-preclean: 0.006/0.269 secs：CMS预清理

```
指定1gxmx和xms不会发生sweep之后操作，是不是预清理之后的内存达到一定阈值才会清理？
```

指定512xmx和xms：java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx512m -Xms512m -XX:+UseConcMarkSweepGC GCLogAnalysis

```
正在执行...
2021-08-15T01:20:48.047+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.047+0800: [ParNew: 139217K->17471K(157248K), 0.0076798 secs] 139217K->43824K(506816K), 0.0080049 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2021-08-15T01:20:48.075+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.075+0800: [ParNew: 157247K->17471K(157248K), 0.0132784 secs] 183600K->89824K(506816K), 0.0142275 secs] [Times: user=0.05 sys=0.11, real=0.01 secs]
2021-08-15T01:20:48.114+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.115+0800: [ParNew: 157247K->17471K(157248K), 0.0270529 secs] 229600K->142090K(506816K), 0.0277985 secs] [Times: user=0.14 sys=0.02, real=0.03 secs]
2021-08-15T01:20:48.162+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.162+0800: [ParNew: 157247K->17472K(157248K), 0.0222666 secs] 281866K->188438K(506816K), 0.0228039 secs] [Times: user=0.14 sys=0.02, real=0.02 secs]
2021-08-15T01:20:48.210+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.210+0800: [ParNew: 157248K->17472K(157248K), 0.0214831 secs] 328214K->236473K(506816K), 0.0223494 secs] [Times: user=0.09 sys=0.03, real=0.02 secs]
2021-08-15T01:20:48.233+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 219001K(349568K)] 239781K(506816K), 0.0005943 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.234+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.235+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.03 sys=0.03, real=0.00 secs]
2021-08-15T01:20:48.236+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.238+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.239+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.261+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.261+0800: [ParNew: 157248K->17471K(157248K), 0.0209118 secs] 376249K->280432K(506816K), 0.0213833 secs] [Times: user=0.16 sys=0.01, real=0.02 secs]
2021-08-15T01:20:48.303+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.303+0800: [ParNew: 157247K->17471K(157248K), 0.0231486 secs] 420208K->326651K(506816K), 0.0235747 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
2021-08-15T01:20:48.347+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.347+0800: [ParNew: 157247K->157247K(157248K), 0.0002150 secs]2021-08-15T01:20:48.347+0800: [CMS2021-08-15T01:20:48.347+0800: [CMS-concurrent-abortable-preclean: 0.003/0.107 secs] [Times: user=0.39 sys=0.01, real=0.11 secs]
 (concurrent mode failure): 309180K->257896K(349568K), 0.0394168 secs] 466427K->257896K(506816K), [Metaspace: 2704K->2704K(1056768K)], 0.0402873 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]
2021-08-15T01:20:48.406+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.407+0800: [ParNew: 139776K->17471K(157248K), 0.0105239 secs] 397672K->303831K(506816K), 0.0113602 secs] [Times: user=0.16 sys=0.00, real=0.01 secs]
2021-08-15T01:20:48.418+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 286360K(349568K)] 307159K(506816K), 0.0006765 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.419+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.422+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.424+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.426+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.426+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.445+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.446+0800: [ParNew: 157247K->17469K(157248K), 0.0195889 secs] 443607K->349533K(506816K), 0.0202921 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
2021-08-15T01:20:48.466+0800: [CMS-concurrent-abortable-preclean: 0.001/0.039 secs] [Times: user=0.17 sys=0.00, real=0.04 secs]
2021-08-15T01:20:48.467+0800: [GC (CMS Final Remark) [YG occupancy: 32113 K (157248 K)]2021-08-15T01:20:48.467+0800: [Rescan (parallel) , 0.0004031 secs]2021-08-15T01:20:48.467+0800: [weak refs processing, 0.0001945 secs]2021-08-15T01:20:48.468+0800: [class unloading, 0.0003366 secs]2021-08-15T01:20:48.468+0800: [scrub symbol table, 0.0004916 secs]2021-08-15T01:20:48.469+0800: [scrub string table, 0.0002211 secs][1 CMS-remark: 332064K(349568K)] 364177K(506816K), 0.0021437 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.469+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.470+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.03 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.471+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.472+0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.497+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.497+0800: [ParNew: 157121K->17470K(157248K), 0.0139762 secs] 450683K->349833K(506816K), 0.0142831 secs] [Times: user=0.16 sys=0.00, real=0.01 secs]
2021-08-15T01:20:48.512+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 332363K(349568K)] 353419K(506816K), 0.0004692 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.513+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.514+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.515+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.516+0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.516+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.516+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.516+0800: [GC (CMS Final Remark) [YG occupancy: 50534 K (157248 K)]2021-08-15T01:20:48.517+0800: [Rescan (parallel) , 0.0004027 secs]2021-08-15T01:20:48.517+0800: [weak refs processing, 0.0001974 secs]2021-08-15T01:20:48.517+0800: [class unloading, 0.0003812 secs]2021-08-15T01:20:48.518+0800: [scrub symbol table, 0.0004999 secs]2021-08-15T01:20:48.518+0800: [scrub string table, 0.0003489 secs][1 CMS-remark: 332363K(349568K)] 382898K(506816K), 0.0024429 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.519+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.520+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.521+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.521+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.535+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.535+0800: [ParNew: 157154K->17464K(157248K), 0.0143460 secs] 426063K->329569K(506816K), 0.0146825 secs] [Times: user=0.16 sys=0.00, real=0.01 secs]
2021-08-15T01:20:48.550+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 312104K(349568K)] 332634K(506816K), 0.0006560 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.551+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.553+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.555+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.556+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.557+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.557+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.558+0800: [GC (CMS Final Remark) [YG occupancy: 47711 K (157248 K)]2021-08-15T01:20:48.559+0800: [Rescan (parallel) , 0.0006792 secs]2021-08-15T01:20:48.559+0800: [weak refs processing, 0.0003180 secs]2021-08-15T01:20:48.560+0800: [class unloading, 0.0010041 secs]2021-08-15T01:20:48.561+0800: [scrub symbol table, 0.0006456 secs]2021-08-15T01:20:48.562+0800: [scrub string table, 0.0002207 secs][1 CMS-remark: 312104K(349568K)] 359815K(506816K), 0.0035685 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.562+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.563+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.563+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.563+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.585+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.586+0800: [ParNew: 157240K->17470K(157248K), 0.0152489 secs] 433548K->339984K(506816K), 0.0155344 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
2021-08-15T01:20:48.601+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 322514K(349568K)] 340128K(506816K), 0.0004563 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.602+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.604+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.605+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.607+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.607+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.607+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.608+0800: [GC (CMS Final Remark) [YG occupancy: 43634 K (157248 K)]2021-08-15T01:20:48.609+0800: [Rescan (parallel) , 0.0008753 secs]2021-08-15T01:20:48.610+0800: [weak refs processing, 0.0004218 secs]2021-08-15T01:20:48.610+0800: [class unloading, 0.0008244 secs]2021-08-15T01:20:48.611+0800: [scrub symbol table, 0.0006162 secs]2021-08-15T01:20:48.612+0800: [scrub string table, 0.0002040 secs][1 CMS-remark: 322514K(349568K)] 366148K(506816K), 0.0038915 secs] [Times: user=0.00 sys=0.03, real=0.00 secs]
2021-08-15T01:20:48.613+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.613+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.614+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.614+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.633+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.633+0800: [ParNew: 157246K->17470K(157248K), 0.0166249 secs] 448177K->352630K(506816K), 0.0169467 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
2021-08-15T01:20:48.651+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 335160K(349568K)] 352639K(506816K), 0.0005601 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.652+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.653+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.654+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.655+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.656+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.656+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.657+0800: [GC (CMS Final Remark) [YG occupancy: 36043 K (157248 K)]2021-08-15T01:20:48.657+0800: [Rescan (parallel) , 0.0006525 secs]2021-08-15T01:20:48.658+0800: [weak refs processing, 0.0002517 secs]2021-08-15T01:20:48.658+0800: [class unloading, 0.0008595 secs]2021-08-15T01:20:48.659+0800: [scrub symbol table, 0.0009351 secs]2021-08-15T01:20:48.660+0800: [scrub string table, 0.0002718 secs][1 CMS-remark: 335160K(349568K)] 371204K(506816K), 0.0036925 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.661+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.662+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.662+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.663+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.690+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.690+0800: [ParNew: 157246K->17471K(157248K), 0.0125398 secs] 456999K->356553K(506816K), 0.0131291 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2021-08-15T01:20:48.703+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 339082K(349568K)] 356565K(506816K), 0.0003627 secs] [Times: user=0.00 sys=0.02, real=0.00 secs]
2021-08-15T01:20:48.704+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.706+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.707+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.707+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.708+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.708+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.709+0800: [GC (CMS Final Remark) [YG occupancy: 35745 K (157248 K)]2021-08-15T01:20:48.709+0800: [Rescan (parallel) , 0.0005285 secs]2021-08-15T01:20:48.709+0800: [weak refs processing, 0.0002271 secs]2021-08-15T01:20:48.710+0800: [class unloading, 0.0003497 secs]2021-08-15T01:20:48.710+0800: [scrub symbol table, 0.0004510 secs]2021-08-15T01:20:48.711+0800: [scrub string table, 0.0002044 secs][1 CMS-remark: 339082K(349568K)] 374827K(506816K), 0.0024179 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.711+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.712+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.713+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.713+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.732+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.732+0800: [ParNew: 157247K->157247K(157248K), 0.0005934 secs]2021-08-15T01:20:48.733+0800: [CMS: 304875K->322916K(349568K), 0.0527261 secs] 462123K->322916K(506816K), [Metaspace: 2704K->2704K(1056768K)], 0.0541400 secs] [Times: user=0.06 sys=0.00, real=0.05 secs]
2021-08-15T01:20:48.786+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 322916K(349568K)] 325755K(506816K), 0.0004972 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.787+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.789+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.789+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.789+0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.790+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.790+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.790+0800: [GC (CMS Final Remark) [YG occupancy: 33154 K (157248 K)]2021-08-15T01:20:48.790+0800: [Rescan (parallel) , 0.0004097 secs]2021-08-15T01:20:48.791+0800: [weak refs processing, 0.0002625 secs]2021-08-15T01:20:48.791+0800: [class unloading, 0.0004147 secs]2021-08-15T01:20:48.792+0800: [scrub symbol table, 0.0004296 secs]2021-08-15T01:20:48.792+0800: [scrub string table, 0.0002394 secs][1 CMS-remark: 322916K(349568K)] 356070K(506816K), 0.0023213 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.792+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.793+0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.793+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.793+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.806+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.806+0800: [ParNew: 139776K->17467K(157248K), 0.0062574 secs] 459238K->361038K(506816K), 0.0065832 secs] [Times: user=0.16 sys=0.00, real=0.01 secs]
2021-08-15T01:20:48.813+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 343570K(349568K)] 361074K(506816K), 0.0004821 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.814+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.815+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.816+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.817+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.817+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.818+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.818+0800: [GC (CMS Final Remark) [YG occupancy: 35061 K (157248 K)]2021-08-15T01:20:48.818+0800: [Rescan (parallel) , 0.0003178 secs]2021-08-15T01:20:48.819+0800: [weak refs processing, 0.0001588 secs]2021-08-15T01:20:48.819+0800: [class unloading, 0.0002910 secs]2021-08-15T01:20:48.819+0800: [scrub symbol table, 0.0004932 secs]2021-08-15T01:20:48.820+0800: [scrub string table, 0.0003810 secs][1 CMS-remark: 343570K(349568K)] 378632K(506816K), 0.0021747 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.821+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.823+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.823+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.824+0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.844+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.844+0800: [ParNew (promotion failed): 157243K->157245K(157248K), 0.0080982 secs]2021-08-15T01:20:48.853+0800: [CMS: 345089K->343348K(349568K), 0.0591904 secs] 469901K->343348K(506816K), [Metaspace: 2704K->2704K(1056768K)], 0.0679778 secs] [Times: user=0.06 sys=0.00, real=0.07 secs]
2021-08-15T01:20:48.912+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 343348K(349568K)] 343726K(506816K), 0.0003005 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.913+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.914+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.914+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.915+0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.915+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.915+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.916+0800: [GC (CMS Final Remark) [YG occupancy: 22229 K (157248 K)]2021-08-15T01:20:48.916+0800: [Rescan (parallel) , 0.0003309 secs]2021-08-15T01:20:48.916+0800: [weak refs processing, 0.0001780 secs]2021-08-15T01:20:48.916+0800: [class unloading, 0.0003855 secs]2021-08-15T01:20:48.917+0800: [scrub symbol table, 0.0004744 secs]2021-08-15T01:20:48.917+0800: [scrub string table, 0.0002212 secs][1 CMS-remark: 343348K(349568K)] 365577K(506816K), 0.0021165 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.918+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.919+0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.919+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.919+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.931+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.931+0800: [ParNew: 139776K->139776K(157248K), 0.0002353 secs]2021-08-15T01:20:48.931+0800: [CMS: 339886K->348494K(349568K), 0.0550163 secs] 479662K->348494K(506816K), [Metaspace: 2704K->2704K(1056768K)], 0.0558825 secs] [Times: user=0.06 sys=0.00, real=0.06 secs]
2021-08-15T01:20:48.987+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 348494K(349568K)] 348599K(506816K), 0.0005887 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.988+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.989+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.990+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.990+0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.990+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.991+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.991+0800: [GC (CMS Final Remark) [YG occupancy: 25448 K (157248 K)]2021-08-15T01:20:48.991+0800: [Rescan (parallel) , 0.0003880 secs]2021-08-15T01:20:48.992+0800: [weak refs processing, 0.0003185 secs]2021-08-15T01:20:48.992+0800: [class unloading, 0.0003764 secs]2021-08-15T01:20:48.993+0800: [scrub symbol table, 0.0004679 secs]2021-08-15T01:20:48.993+0800: [scrub string table, 0.0002165 secs][1 CMS-remark: 348494K(349568K)] 373943K(506816K), 0.0024864 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.994+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.994+0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.995+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.995+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
执行结束!共生成对象次数:10076
Heap
 par new generation   total 157248K, used 41474K [0x00000000e0000000, 0x00000000eaaa0000, 0x00000000eaaa0000)
  eden space 139776K,  29% used [0x00000000e0000000, 0x00000000e2880a80, 0x00000000e8880000)
  from space 17472K,   0% used [0x00000000e8880000, 0x00000000e8880000, 0x00000000e9990000)
  to   space 17472K,   0% used [0x00000000e9990000, 0x00000000e9990000, 0x00000000eaaa0000)
 concurrent mark-sweep generation total 349568K, used 347795K [0x00000000eaaa0000, 0x0000000100000000, 0x0000000100000000)
 Metaspace       used 2710K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 291K, capacity 386K, committed 512K, reserved 1048576K
```



```
第一次老年代GC
2021-08-15T01:20:48.233+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 219001K(349568K)] 239781K(506816K), 0.0005943 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.234+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.235+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.03 sys=0.03, real=0.00 secs]
2021-08-15T01:20:48.236+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.238+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.239+0800: [CMS-concurrent-abortable-preclean-start]
...
2021-08-15T01:20:48.347+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.347+0800: [ParNew: 157247K->157247K(157248K), 0.0002150 secs]2021-08-15T01:20:48.347+0800: [CMS2021-08-15T01:20:48.347+0800: [CMS-concurrent-abortable-preclean: 0.003/0.107 secs] [Times: user=0.39 sys=0.01, real=0.11 secs]
并发模式失败 退化成串行GC
 (concurrent mode failure): 309180K->257896K(349568K), 0.0394168 secs] 466427K->257896K(506816K), [Metaspace: 2704K->2704K(1056768K)], 0.0402873 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]
只有预清理 未清理
...
第二次老年代GC
2021-08-15T01:20:48.418+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 286360K(349568K)] 307159K(506816K), 0.0006765 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.419+0800: [CMS-concurrent-mark-start]
2021-08-15T01:20:48.422+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.424+0800: [CMS-concurrent-preclean-start]
2021-08-15T01:20:48.426+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T01:20:48.426+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-15T01:20:48.445+0800: [GC (Allocation Failure) 2021-08-15T01:20:48.446+0800: [ParNew: 157247K->17469K(157248K), 0.0195889 secs] 443607K->349533K(506816K), 0.0202921 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
2021-08-15T01:20:48.466+0800: [CMS-concurrent-abortable-preclean: 0.001/0.039 secs] [Times: user=0.17 sys=0.00, real=0.04 secs]
最终标记
2021-08-15T01:20:48.467+0800: [GC (CMS Final Remark) [YG occupancy: 32113 K (157248 K)]2021-08-15T01:20:48.467+0800: [Rescan (parallel) , 0.0004031 secs]2021-08-15T01:20:48.467+0800: [weak refs processing, 0.0001945 secs]2021-08-15T01:20:48.468+0800: [class unloading, 0.0003366 secs]2021-08-15T01:20:48.468+0800: [scrub symbol table, 0.0004916 secs]2021-08-15T01:20:48.469+0800: [scrub string table, 0.0002211 secs][1 CMS-remark: 332064K(349568K)] 364177K(506816K), 0.0021437 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
开始CMS并发清理
2021-08-15T01:20:48.469+0800: [CMS-concurrent-sweep-start]
2021-08-15T01:20:48.470+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.03 sys=0.00, real=0.00 secs]
开始重置
2021-08-15T01:20:48.471+0800: [CMS-concurrent-reset-start]
2021-08-15T01:20:48.472+0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
继续下一次
```

并发预清理：**此阶段同样是与应用线程并发执行的，不需要停止应用线程。 因为前一阶段【并发标记】与程序并发运行，可能有一些引用关系已经发生了改变。如果在并发标记过程中引用关系发生了变化，JVM 会通过“Card（卡片）”的方式将发生了改变的区域标记为“脏”区，这就是所谓的 卡片标记（Card Marking）。**

```
问题：并发预清理后会初始化标记，也会最终标记，时机是？？？
答：并发预清理之后，young区有可能还在做GC，如果这时的old区的剩余空间不足以容纳young区晋升的对象，old区的垃圾收集器会从CMS退化成Serial串行垃圾回收器，执行old区的垃圾回收。
```



java命令-指定G1： java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx1g -Xms1g -XX:+UseG1GC GCLogAnalysis

```
正在执行...
2021-08-15T14:41:11.200+0800: [GC pause (G1 Evacuation Pause) (young), 0.0051834 secs]
   [Parallel Time: 3.8 ms, GC Workers: 10]
      [GC Worker Start (ms): Min: 112.1, Avg: 112.2, Max: 112.2, Diff: 0.1]
      [Ext Root Scanning (ms): Min: 0.1, Avg: 0.1, Max: 0.2, Diff: 0.1, Sum: 1.1]
      [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
         [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 3.0, Avg: 3.2, Max: 3.5, Diff: 0.5, Sum: 32.3]
      [Termination (ms): Min: 0.0, Avg: 0.3, Max: 0.5, Diff: 0.5, Sum: 2.6]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 10]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.0, Sum: 0.3]
      [GC Worker Total (ms): Min: 3.6, Avg: 3.6, Max: 3.7, Diff: 0.1, Sum: 36.4]
      [GC Worker End (ms): Min: 115.8, Avg: 115.8, Max: 115.8, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.2 ms]
   [Other: 1.1 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.2 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.2 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.1 ms]
      [Free CSet: 0.1 ms]
   [Eden: 51.0M(51.0M)->0.0B(44.0M) Survivors: 0.0B->7168.0K Heap: 63.9M(1024.0M)->22.4M(1024.0M)]
 [Times: user=0.11 sys=0.02, real=0.01 secs]
2021-08-15T14:41:11.218+0800: [GC pause (G1 Evacuation Pause) (young), 0.0062741 secs]
   [Parallel Time: 4.8 ms, GC Workers: 10]
      [GC Worker Start (ms): Min: 130.6, Avg: 130.7, Max: 130.8, Diff: 0.3]
      [Ext Root Scanning (ms): Min: 0.1, Avg: 0.2, Max: 0.5, Diff: 0.4, Sum: 2.2]
      [Update RS (ms): Min: 0.0, Avg: 0.2, Max: 1.3, Diff: 1.3, Sum: 2.1]
         [Processed Buffers: Min: 0, Avg: 1.7, Max: 3, Diff: 3, Sum: 17]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]
      [Object Copy (ms): Min: 3.0, Avg: 4.0, Max: 4.4, Diff: 1.4, Sum: 39.8]
      [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.3, Diff: 0.3, Sum: 1.5]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 10]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.2]
      [GC Worker Total (ms): Min: 4.5, Avg: 4.6, Max: 4.7, Diff: 0.3, Sum: 45.9]
      [GC Worker End (ms): Min: 135.3, Avg: 135.3, Max: 135.3, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.2 ms]
   [Other: 1.3 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.2 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.3 ms]
      [Humongous Register: 0.1 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 44.0M(44.0M)->0.0B(44.0M) Survivors: 7168.0K->7168.0K Heap: 83.2M(1024.0M)->42.6M(1024.0M)]
 [Times: user=0.13 sys=0.02, real=0.01 secs]
 
 ...
 
 2021-08-15T14:41:12.050+0800: [GC pause (G1 Evacuation Pause) (mixed), 0.0098852 secs]
   [Parallel Time: 7.3 ms, GC Workers: 10]
      [GC Worker Start (ms): Min: 962.0, Avg: 962.1, Max: 962.1, Diff: 0.1]
      [Ext Root Scanning (ms): Min: 0.1, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 1.0]
      [Update RS (ms): Min: 0.4, Avg: 0.4, Max: 0.4, Diff: 0.0, Sum: 3.9]
         [Processed Buffers: Min: 0, Avg: 4.9, Max: 7, Diff: 7, Sum: 49]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.3]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 6.6, Avg: 6.7, Max: 6.7, Diff: 0.1, Sum: 66.5]
      [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.4]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 10]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [GC Worker Total (ms): Min: 7.2, Avg: 7.2, Max: 7.3, Diff: 0.1, Sum: 72.3]
      [GC Worker End (ms): Min: 969.3, Avg: 969.3, Max: 969.3, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.8 ms]
   [Other: 1.8 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.2 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.4 ms]
      [Humongous Register: 0.1 ms]
      [Humongous Reclaim: 0.2 ms]
      [Free CSet: 0.5 ms]
   [Eden: 44.0M(44.0M)->0.0B(44.0M) Survivors: 7168.0K->7168.0K Heap: 652.7M(1024.0M)->526.9M(1024.0M)]
 [Times: user=0.00 sys=0.00, real=0.01 secs]
2021-08-15T14:41:12.073+0800: [GC pause (G1 Evacuation Pause) (mixed), 0.0102368 secs]
   [Parallel Time: 8.3 ms, GC Workers: 10]
      [GC Worker Start (ms): Min: 984.7, Avg: 984.7, Max: 984.8, Diff: 0.1]
      [Ext Root Scanning (ms): Min: 0.1, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 1.1]
      [Update RS (ms): Min: 0.5, Avg: 0.5, Max: 0.7, Diff: 0.2, Sum: 5.1]
         [Processed Buffers: Min: 0, Avg: 6.8, Max: 11, Diff: 11, Sum: 68]
      [Scan RS (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 7.3, Avg: 7.4, Max: 7.5, Diff: 0.2, Sum: 73.8]
      [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.6]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 10]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [GC Worker Total (ms): Min: 8.1, Avg: 8.1, Max: 8.2, Diff: 0.1, Sum: 81.3]
      [GC Worker End (ms): Min: 992.9, Avg: 992.9, Max: 992.9, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.6 ms]
   [Other: 1.3 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.5 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.3 ms]
      [Humongous Register: 0.1 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.1 ms]
   [Eden: 44.0M(44.0M)->0.0B(294.0M) Survivors: 7168.0K->7168.0K Heap: 584.2M(1024.0M)->492.9M(1024.0M)]
 [Times: user=0.16 sys=0.00, real=0.01 secs]
2021-08-15T14:41:12.088+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark), 0.0021056 secs]
   [Parallel Time: 1.5 ms, GC Workers: 10]
      [GC Worker Start (ms): Min: 999.8, Avg: 999.8, Max: 999.9, Diff: 0.1]
      [Ext Root Scanning (ms): Min: 0.1, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.9]
      [Update RS (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.0]
         [Processed Buffers: Min: 1, Avg: 1.1, Max: 2, Diff: 1, Sum: 11]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 1.1, Avg: 1.2, Max: 1.3, Diff: 0.2, Sum: 11.7]
      [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.7]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 10]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [GC Worker Total (ms): Min: 1.4, Avg: 1.4, Max: 1.5, Diff: 0.1, Sum: 14.3]
      [GC Worker End (ms): Min: 1001.3, Avg: 1001.3, Max: 1001.3, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 0.5 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.1 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.1 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 7168.0K(294.0M)->0.0B(278.0M) Survivors: 7168.0K->10.0M Heap: 499.6M(1024.0M)->494.6M(1024.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T14:41:12.092+0800: [GC concurrent-root-region-scan-start]
2021-08-15T14:41:12.092+0800: [GC concurrent-root-region-scan-end, 0.0002329 secs]
2021-08-15T14:41:12.092+0800: [GC concurrent-mark-start]
2021-08-15T14:41:12.094+0800: [GC concurrent-mark-end, 0.0014875 secs]
2021-08-15T14:41:12.094+0800: [GC remark 2021-08-15T14:41:12.094+0800: [Finalize Marking, 0.0002111 secs] 2021-08-15T14:41:12.095+0800: [GC ref-proc, 0.0002662 secs] 2021-08-15T14:41:12.095+0800: [Unloading, 0.0006143 secs], 0.0021047 secs]
 [Times: user=0.00 sys=0.02, real=0.00 secs]
2021-08-15T14:41:12.096+0800: [GC cleanup 510M->505M(1024M), 0.0008744 secs]
 [Times: user=0.00 sys=0.00, real=0.00 secs]
2021-08-15T14:41:12.097+0800: [GC concurrent-cleanup-start]
2021-08-15T14:41:12.098+0800: [GC concurrent-cleanup-end, 0.0001747 secs]
2021-08-15T14:41:12.153+0800: [GC pause (G1 Evacuation Pause) (young) (to-space exhausted), 0.0171262 secs]
   [Parallel Time: 14.7 ms, GC Workers: 10]
      [GC Worker Start (ms): Min: 1065.1, Avg: 1065.2, Max: 1065.2, Diff: 0.1]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.9]
      [Update RS (ms): Min: 0.4, Avg: 0.6, Max: 1.4, Diff: 1.0, Sum: 5.6]
         [Processed Buffers: Min: 0, Avg: 6.2, Max: 8, Diff: 8, Sum: 62]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.0, Sum: 0.4]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 13.0, Avg: 13.8, Max: 13.9, Diff: 1.0, Sum: 138.0]
      [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.4]
         [Termination Attempts: Min: 1, Avg: 1.6, Max: 2, Diff: 1, Sum: 16]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.3]
      [GC Worker Total (ms): Min: 14.5, Avg: 14.6, Max: 14.6, Diff: 0.1, Sum: 145.5]
      [GC Worker End (ms): Min: 1079.7, Avg: 1079.7, Max: 1079.7, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.2 ms]
   [Other: 2.2 ms]
      [Evacuation Failure: 0.2 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.4 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.2 ms]
      [Humongous Register: 0.2 ms]
      [Humongous Reclaim: 0.3 ms]
      [Free CSet: 0.2 ms]
   [Eden: 278.0M(278.0M)->0.0B(15.0M) Survivors: 10.0M->36.0M Heap: 842.9M(1024.0M)->601.6M(1024.0M)]
 [Times: user=0.17 sys=0.00, real=0.02 secs]
执行结束!共生成对象次数:13322
Heap
 garbage-first heap   total 1048576K, used 615990K [0x00000000c0000000, 0x00000000c0102000, 0x0000000100000000)
  region size 1024K, 37 young (37888K), 36 survivors (36864K)
 Metaspace       used 2710K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 291K, capacity 386K, committed 512K, reserved 1048576K
```

java命令-打印非详细信息： java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx1g -Xms1g -XX:+UseG1GC GCLogAnalysis

```
正在执行...
2021-08-15T14:47:00.651+0800: [GC pause (G1 Evacuation Pause) (young) 60M->22M(1024M), 0.0040835 secs]
2021-08-15T14:47:00.667+0800: [GC pause (G1 Evacuation Pause) (young) 78M->43M(1024M), 0.0043871 secs]
2021-08-15T14:47:00.685+0800: [GC pause (G1 Evacuation Pause) (young) 96M->58M(1024M), 0.0041788 secs]
2021-08-15T14:47:00.710+0800: [GC pause (G1 Evacuation Pause) (young) 131M->85M(1024M), 0.0090519 secs]
2021-08-15T14:47:00.751+0800: [GC pause (G1 Evacuation Pause) (young) 205M->123M(1024M), 0.0064664 secs]
2021-08-15T14:47:00.786+0800: [GC pause (G1 Evacuation Pause) (young) 259M->165M(1024M), 0.0092153 secs]
2021-08-15T14:47:00.828+0800: [GC pause (G1 Evacuation Pause) (young) 335M->217M(1024M), 0.0108920 secs]
2021-08-15T14:47:00.877+0800: [GC pause (G1 Evacuation Pause) (young) 412M->272M(1024M), 0.0118890 secs]
2021-08-15T14:47:00.934+0800: [GC pause (G1 Evacuation Pause) (young) 516M->330M(1024M), 0.0143060 secs]
2021-08-15T14:47:01.003+0800: [GC pause (G1 Evacuation Pause) (young) 641M->400M(1024M), 0.0134808 secs]
2021-08-15T14:47:01.036+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 546M->439M(1024M), 0.0125608 secs]
2021-08-15T14:47:01.049+0800: [GC concurrent-root-region-scan-start]
2021-08-15T14:47:01.050+0800: [GC concurrent-root-region-scan-end, 0.0012949 secs]
2021-08-15T14:47:01.050+0800: [GC concurrent-mark-start]
2021-08-15T14:47:01.053+0800: [GC concurrent-mark-end, 0.0026124 secs]
2021-08-15T14:47:01.053+0800: [GC remark, 0.0013320 secs]
2021-08-15T14:47:01.055+0800: [GC cleanup 459M->448M(1024M), 0.0009379 secs]
2021-08-15T14:47:01.056+0800: [GC concurrent-cleanup-start]
2021-08-15T14:47:01.056+0800: [GC concurrent-cleanup-end, 0.0001755 secs]
2021-08-15T14:47:01.140+0800: [GC pause (G1 Evacuation Pause) (young)-- 842M->595M(1024M), 0.0149460 secs]
2021-08-15T14:47:01.155+0800: [GC pause (G1 Evacuation Pause) (mixed) 600M->510M(1024M), 0.0106955 secs]
2021-08-15T14:47:01.173+0800: [GC pause (G1 Evacuation Pause) (mixed) 576M->514M(1024M), 0.0065144 secs]
2021-08-15T14:47:01.180+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 515M->514M(1024M), 0.0026056 secs]
2021-08-15T14:47:01.183+0800: [GC concurrent-root-region-scan-start]
2021-08-15T14:47:01.183+0800: [GC concurrent-root-region-scan-end, 0.0002918 secs]
2021-08-15T14:47:01.183+0800: [GC concurrent-mark-start]
2021-08-15T14:47:01.185+0800: [GC concurrent-mark-end, 0.0016572 secs]
2021-08-15T14:47:01.186+0800: [GC remark, 0.0012316 secs]
2021-08-15T14:47:01.187+0800: [GC cleanup 535M->525M(1024M), 0.0011232 secs]
2021-08-15T14:47:01.188+0800: [GC concurrent-cleanup-start]
2021-08-15T14:47:01.189+0800: [GC concurrent-cleanup-end, 0.0002894 secs]
2021-08-15T14:47:01.240+0800: [GC pause (G1 Evacuation Pause) (young) 846M->577M(1024M), 0.0117283 secs]
2021-08-15T14:47:01.255+0800: [GC pause (G1 Evacuation Pause) (mixed) 597M->491M(1024M), 0.0090197 secs]
2021-08-15T14:47:01.271+0800: [GC pause (G1 Evacuation Pause) (mixed) 542M->446M(1024M), 0.0093618 secs]
2021-08-15T14:47:01.281+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 449M->448M(1024M), 0.0020630 secs]
2021-08-15T14:47:01.283+0800: [GC concurrent-root-region-scan-start]
2021-08-15T14:47:01.283+0800: [GC concurrent-root-region-scan-end, 0.0002565 secs]
2021-08-15T14:47:01.283+0800: [GC concurrent-mark-start]
2021-08-15T14:47:01.285+0800: [GC concurrent-mark-end, 0.0014706 secs]
2021-08-15T14:47:01.285+0800: [GC remark, 0.0010632 secs]
2021-08-15T14:47:01.286+0800: [GC cleanup 468M->465M(1024M), 0.0006304 secs]
2021-08-15T14:47:01.287+0800: [GC concurrent-cleanup-start]
2021-08-15T14:47:01.287+0800: [GC concurrent-cleanup-end, 0.0002400 secs]
2021-08-15T14:47:01.352+0800: [GC pause (G1 Evacuation Pause) (young)-- 856M->632M(1024M), 0.0104885 secs]
2021-08-15T14:47:01.364+0800: [GC pause (G1 Evacuation Pause) (mixed) 642M->559M(1024M), 0.0113321 secs]
2021-08-15T14:47:01.384+0800: [GC pause (G1 Evacuation Pause) (mixed) 615M->572M(1024M), 0.0038231 secs]
2021-08-15T14:47:01.389+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 580M->573M(1024M), 0.0023672 secs]
2021-08-15T14:47:01.392+0800: [GC concurrent-root-region-scan-start]
2021-08-15T14:47:01.392+0800: [GC concurrent-root-region-scan-end, 0.0005062 secs]
2021-08-15T14:47:01.393+0800: [GC concurrent-mark-start]
2021-08-15T14:47:01.396+0800: [GC concurrent-mark-end, 0.0030201 secs]
2021-08-15T14:47:01.396+0800: [GC remark, 0.0016237 secs]
2021-08-15T14:47:01.398+0800: [GC cleanup 591M->579M(1024M), 0.0008420 secs]
2021-08-15T14:47:01.399+0800: [GC concurrent-cleanup-start]
2021-08-15T14:47:01.399+0800: [GC concurrent-cleanup-end, 0.0003298 secs]
2021-08-15T14:47:01.449+0800: [GC pause (G1 Evacuation Pause) (young) 855M->622M(1024M), 0.0099188 secs]
2021-08-15T14:47:01.465+0800: [GC pause (G1 Evacuation Pause) (mixed) 647M->533M(1024M), 0.0087680 secs]
2021-08-15T14:47:01.481+0800: [GC pause (G1 Evacuation Pause) (mixed) 586M->473M(1024M), 0.0087208 secs]
2021-08-15T14:47:01.502+0800: [GC pause (G1 Evacuation Pause) (mixed) 532M->480M(1024M), 0.0064556 secs]
2021-08-15T14:47:01.509+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 482M->479M(1024M), 0.0019874 secs]
2021-08-15T14:47:01.512+0800: [GC concurrent-root-region-scan-start]
2021-08-15T14:47:01.512+0800: [GC concurrent-root-region-scan-end, 0.0004002 secs]
2021-08-15T14:47:01.512+0800: [GC concurrent-mark-start]
2021-08-15T14:47:01.514+0800: [GC concurrent-mark-end, 0.0019832 secs]
2021-08-15T14:47:01.514+0800: [GC remark, 0.0017961 secs]
2021-08-15T14:47:01.517+0800: [GC cleanup 494M->491M(1024M), 0.0016059 secs]
2021-08-15T14:47:01.518+0800: [GC concurrent-cleanup-start]
2021-08-15T14:47:01.519+0800: [GC concurrent-cleanup-end, 0.0002841 secs]
2021-08-15T14:47:01.593+0800: [GC pause (G1 Evacuation Pause) (young)-- 867M->698M(1024M), 0.0183180 secs]
2021-08-15T14:47:01.613+0800: [GC pause (G1 Evacuation Pause) (mixed) 712M->625M(1024M), 0.0113485 secs]
执行结束!共生成对象次数:13789
```

1gxms、xmx下

- Parrallel GC ：12次young gc（0.02左右）、1次old gc（0.077）、7次young gc（0.02左右）
- Serial GC：12次young gc（0.035左右）、1次old gc（0.060）、7次young gc（<0.02）
- CMS GC：9次young gc（0.013开始递增到0.36左右）、CMS-initial-mark（0.0007）、CMS-concurrent-mark（0.002）、CMS-concurrent-preclean（0.006）、1次退化Serial GC（0.050）、7次young gc（<0.02）。未发生sweep。CMS GC每次用时短，但是退化成串行GC耗费时间长
- G1 GC ：youny gc（0.004开始递增至0.01左右）次数很多（9次之后开始young模式和mixed模式并行发生），会发生Humongous Allocation（大对象分配失败），old区region空间不足以分配晋升的对象，触发mixed，mixed模式gc和young模式gc时间差不多 

分配速率：年轻代上面的新对象的分配~~（与管理未使用内存的规则相关？）~~

晋升速率：年轻代晋升至老年代的速率



