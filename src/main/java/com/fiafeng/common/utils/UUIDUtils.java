package com.fiafeng.common.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 提供通用唯一识别码（universally unique identifier）（UUIDUtils）实现
 *
 * @author ruoyi
 */
public final class UUIDUtils implements java.io.Serializable, Comparable<UUIDUtils> {
    private static final long serialVersionUID = -1185015143654744140L;

    /**
     * SecureRandom 的单例
     */
    private static class Holder {
        static final SecureRandom numberGenerator = getSecureRandom();
    }

    /**
     * 此UUID的最高64有效位
     */
    private final long mostSigBits;

    /**
     * 此UUID的最低64有效位
     */
    private final long leastSigBits;

    /**
     * 私有构造
     *
     * @param data 数据
     */
    private UUIDUtils(byte[] data) {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (data[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (data[i] & 0xff);
        }
        this.mostSigBits = msb;
        this.leastSigBits = lsb;
    }

    /**
     * 使用指定的数据构造新的 UUIDUtils。
     *
     * @param mostSigBits  用于 {@code UUIDUtils} 的最高有效 64 位
     * @param leastSigBits 用于 {@code UUIDUtils} 的最低有效 64 位
     */
    public UUIDUtils(long mostSigBits, long leastSigBits) {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    /**
     * 获取类型 4（伪随机生成的）UUIDUtils 的静态工厂。
     *
     * @return 随机生成的 {@code UUIDUtils}
     */
    public static UUIDUtils fastUUID() {
        return randomUUID(false);
    }

    /**
     * 获取类型 4（伪随机生成的）UUIDUtils 的静态工厂。 使用加密的强伪随机数生成器生成该 UUIDUtils。
     *
     * @return 随机生成的 {@code UUIDUtils}
     */
    public static UUIDUtils randomUUID() {
        return randomUUID(true);
    }

    /**
     * 获取类型 4（伪随机生成的）UUIDUtils 的静态工厂。 使用加密的强伪随机数生成器生成该 UUIDUtils。
     *
     * @param isSecure 是否使用{@link SecureRandom}如果是可以获得更安全的随机码，否则可以得到更好的性能
     * @return 随机生成的 {@code UUIDUtils}
     */
    public static UUIDUtils randomUUID(boolean isSecure) {
        final Random ng = isSecure ? Holder.numberGenerator : getRandom();

        byte[] randomBytes = new byte[16];
        ng.nextBytes(randomBytes);
        randomBytes[6] &= 0x0f; /* clear version */
        randomBytes[6] |= 0x40; /* set to version 4 */
        randomBytes[8] &= 0x3f; /* clear variant */
        randomBytes[8] |= (byte) 0x80; /* set to IETF variant */
        return new UUIDUtils(randomBytes);
    }

    /**
     * 根据指定的字节数组获取类型 3（基于名称的）UUIDUtils 的静态工厂。
     *
     * @param name 用于构造 UUIDUtils 的字节数组。
     * @return 根据指定数组生成的 {@code UUIDUtils}
     */
    public static UUIDUtils nameUUIDFromBytes(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported");
        }
        byte[] md5Bytes = md.digest(name);
        md5Bytes[6] &= 0x0f; /* clear version */
        md5Bytes[6] |= 0x30; /* set to version 3 */
        md5Bytes[8] &= 0x3f; /* clear variant */
        md5Bytes[8] |= (byte) 0x80; /* set to IETF variant */
        return new UUIDUtils(md5Bytes);
    }

    /**
     * 根据 {@link #toString()} 方法中描述的字符串标准表示形式创建{@code UUIDUtils}。
     *
     * @param name 指定 {@code UUIDUtils} 字符串
     * @return 具有指定值的 {@code UUIDUtils}
     * @throws IllegalArgumentException 如果 name 与 {@link #toString} 中描述的字符串表示形式不符抛出此异常
     */
    public static UUIDUtils fromString(String name) {
        String[] components = name.split("-");
        if (components.length != 5) {
            throw new IllegalArgumentException("Invalid UUIDUtils string: " + name);
        }
        for (int i = 0; i < 5; i++) {
            components[i] = "0x" + components[i];
        }

        long mostSigBits = Long.decode(components[0]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[1]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[2]);

        long leastSigBits = Long.decode(components[3]);
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(components[4]);

        return new UUIDUtils(mostSigBits, leastSigBits);
    }

    /**
     * 返回此 UUIDUtils 的 128 位值中的最低有效 64 位。
     *
     * @return 此 UUIDUtils 的 128 位值中的最低有效 64 位。
     */
    public long getLeastSignificantBits() {
        return leastSigBits;
    }

    /**
     * 返回此 UUIDUtils 的 128 位值中的最高有效 64 位。
     *
     * @return 此 UUIDUtils 的 128 位值中最高有效 64 位。
     */
    public long getMostSignificantBits() {
        return mostSigBits;
    }

    /**
     * 与此 {@code UUIDUtils} 相关联的版本号. 版本号描述此 {@code UUIDUtils} 是如何生成的。
     * <p>
     * 版本号具有以下含意:
     * <ul>
     * <li>1 基于时间的 UUIDUtils
     * <li>2 DCE 安全 UUIDUtils
     * <li>3 基于名称的 UUIDUtils
     * <li>4 随机生成的 UUIDUtils
     * </ul>
     *
     * @return 此 {@code UUIDUtils} 的版本号
     */
    public int version() {
        // Version is bits masked by 0x000000000000F000 in MS long
        return (int) ((mostSigBits >> 12) & 0x0f);
    }

    /**
     * 与此 {@code UUIDUtils} 相关联的变体号。变体号描述 {@code UUIDUtils} 的布局。
     * <p>
     * 变体号具有以下含意：
     * <ul>
     * <li>0 为 NCS 向后兼容保留
     * <li>2 <a href="http://www.ietf.org/rfc/rfc4122.txt">IETF&nbsp;RFC&nbsp;4122</a>(Leach-Salz), 用于此类
     * <li>6 保留，微软向后兼容
     * <li>7 保留供以后定义使用
     * </ul>
     *
     * @return 此 {@code UUIDUtils} 相关联的变体号
     */
    public int variant() {
        // This field is composed of a varying number of bits.
        // 0 - - Reserved for NCS backward compatibility
        // 1 0 - The IETF aka Leach-Salz variant (used by this class)
        // 1 1 0 Reserved, Microsoft backward compatibility
        // 1 1 1 Reserved for future definition.
        return (int) ((leastSigBits >>> (64 - (leastSigBits >>> 62))) & (leastSigBits >> 63));
    }

    /**
     * 与此 UUIDUtils 相关联的时间戳值。
     *
     * <p>
     * 60 位的时间戳值根据此 {@code UUIDUtils} 的 time_low、time_mid 和 time_hi 字段构造。<br>
     * 所得到的时间戳以 100 毫微秒为单位，从 UTC（通用协调时间） 1582 年 10 月 15 日零时开始。
     *
     * <p>
     * 时间戳值仅在在基于时间的 UUIDUtils（其 version 类型为 1）中才有意义。<br>
     * 如果此 {@code UUIDUtils} 不是基于时间的 UUIDUtils，则此方法抛出 UnsupportedOperationException。
     *
     * @throws UnsupportedOperationException 如果此 {@code UUIDUtils} 不是 version 为 1 的 UUIDUtils。
     */
    public long timestamp() throws UnsupportedOperationException {
        checkTimeBase();
        return (mostSigBits & 0x0FFFL) << 48//
                | ((mostSigBits >> 16) & 0x0FFFFL) << 32//
                | mostSigBits >>> 32;
    }

    /**
     * 与此 UUIDUtils 相关联的时钟序列值。
     *
     * <p>
     * 14 位的时钟序列值根据此 UUIDUtils 的 clock_seq 字段构造。clock_seq 字段用于保证在基于时间的 UUIDUtils 中的时间唯一性。
     * <p>
     * {@code clockSequence} 值仅在基于时间的 UUIDUtils（其 version 类型为 1）中才有意义。 如果此 UUIDUtils 不是基于时间的 UUIDUtils，则此方法抛出
     * UnsupportedOperationException。
     *
     * @return 此 {@code UUIDUtils} 的时钟序列
     * @throws UnsupportedOperationException 如果此 UUIDUtils 的 version 不为 1
     */
    public int clockSequence() throws UnsupportedOperationException {
        checkTimeBase();
        return (int) ((leastSigBits & 0x3FFF000000000000L) >>> 48);
    }

    /**
     * 与此 UUIDUtils 相关的节点值。
     *
     * <p>
     * 48 位的节点值根据此 UUIDUtils 的 node 字段构造。此字段旨在用于保存机器的 IEEE 802 地址，该地址用于生成此 UUIDUtils 以保证空间唯一性。
     * <p>
     * 节点值仅在基于时间的 UUIDUtils（其 version 类型为 1）中才有意义。<br>
     * 如果此 UUIDUtils 不是基于时间的 UUIDUtils，则此方法抛出 UnsupportedOperationException。
     *
     * @return 此 {@code UUIDUtils} 的节点值
     * @throws UnsupportedOperationException 如果此 UUIDUtils 的 version 不为 1
     */
    public long node() throws UnsupportedOperationException {
        checkTimeBase();
        return leastSigBits & 0x0000FFFFFFFFFFFFL;
    }

    /**
     * 返回此{@code UUIDUtils} 的字符串表现形式。
     *
     * <p>
     * UUIDUtils 的字符串表示形式由此 BNF 描述：
     *
     * <pre>
     * {@code
     * UUIDUtils                   = <time_low>-<time_mid>-<time_high_and_version>-<variant_and_sequence>-<node>
     * time_low               = 4*<hexOctet>
     * time_mid               = 2*<hexOctet>
     * time_high_and_version  = 2*<hexOctet>
     * variant_and_sequence   = 2*<hexOctet>
     * node                   = 6*<hexOctet>
     * hexOctet               = <hexDigit><hexDigit>
     * hexDigit               = [0-9a-fA-F]
     * }
     * </pre>
     *
     * </blockquote>
     *
     * @return 此{@code UUIDUtils} 的字符串表现形式
     * @see #toString(boolean)
     */
    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * 返回此{@code UUIDUtils} 的字符串表现形式。
     *
     * <p>
     * UUIDUtils 的字符串表示形式由此 BNF 描述：
     *
     * <pre>
     * {@code
     * UUIDUtils                   = <time_low>-<time_mid>-<time_high_and_version>-<variant_and_sequence>-<node>
     * time_low               = 4*<hexOctet>
     * time_mid               = 2*<hexOctet>
     * time_high_and_version  = 2*<hexOctet>
     * variant_and_sequence   = 2*<hexOctet>
     * node                   = 6*<hexOctet>
     * hexOctet               = <hexDigit><hexDigit>
     * hexDigit               = [0-9a-fA-F]
     * }
     * </pre>
     *
     * </blockquote>
     *
     * @param isSimple 是否简单模式，简单模式为不带'-'的UUID字符串
     * @return 此{@code UUIDUtils} 的字符串表现形式
     */
    public String toString(boolean isSimple) {
        final StringBuilder builder = new StringBuilder(isSimple ? 32 : 36);
        // time_low
        builder.append(digits(mostSigBits >> 32, 8));
        if (!isSimple) {
            builder.append('-');
        }
        // time_mid
        builder.append(digits(mostSigBits >> 16, 4));
        if (!isSimple) {
            builder.append('-');
        }
        // time_high_and_version
        builder.append(digits(mostSigBits, 4));
        if (!isSimple) {
            builder.append('-');
        }
        // variant_and_sequence
        builder.append(digits(leastSigBits >> 48, 4));
        if (!isSimple) {
            builder.append('-');
        }
        // node
        builder.append(digits(leastSigBits, 12));

        return builder.toString();
    }

    /**
     * 返回此 UUIDUtils 的哈希码。
     *
     * @return UUIDUtils 的哈希码值。
     */
    @Override
    public int hashCode() {
        long hilo = mostSigBits ^ leastSigBits;
        return ((int) (hilo >> 32)) ^ (int) hilo;
    }

    /**
     * 将此对象与指定对象比较。
     * <p>
     * 当且仅当参数不为 {@code null}、而是一个 UUIDUtils 对象、具有与此 UUIDUtils 相同的 varriant、包含相同的值（每一位均相同）时，结果才为 {@code true}。
     *
     * @param obj 要与之比较的对象
     * @return 如果对象相同，则返回 {@code true}；否则返回 {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if ((null == obj) || (obj.getClass() != UUIDUtils.class)) {
            return false;
        }
        UUIDUtils id = (UUIDUtils) obj;
        return (mostSigBits == id.mostSigBits && leastSigBits == id.leastSigBits);
    }

    // Comparison Operations

    /**
     * 将此 UUIDUtils 与指定的 UUIDUtils 比较。
     *
     * <p>
     * 如果两个 UUIDUtils 不同，且第一个 UUIDUtils 的最高有效字段大于第二个 UUIDUtils 的对应字段，则第一个 UUIDUtils 大于第二个 UUIDUtils。
     *
     * @param val 与此 UUIDUtils 比较的 UUIDUtils
     * @return 在此 UUIDUtils 小于、等于或大于 val 时，分别返回 -1、0 或 1。
     */
    @Override
    public int compareTo(UUIDUtils val) {
        // The ordering is intentionally set up so that the UUIDs
        // can simply be numerically compared as two numbers
        //
        //
        return (this.mostSigBits < val.mostSigBits ? -1 : //
                (this.mostSigBits > val.mostSigBits ? 1 : //
                        (Long.compare(this.leastSigBits, val.leastSigBits))));
    }

    // -------------------------------------------------------------------------------------------------------------------
    // Private method start

    /**
     * 返回指定数字对应的hex值
     *
     * @param val    值
     * @param digits 位
     * @return 值
     */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

    /**
     * 检查是否为time-based版本UUID
     */
    private void checkTimeBase() {
        if (version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUIDUtils");
        }
    }

    /**
     * 获取{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)
     *
     * @return {@link SecureRandom}
     */
    public static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取随机数生成器对象<br>
     * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
     *
     * @return {@link ThreadLocalRandom}
     */
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }
}
