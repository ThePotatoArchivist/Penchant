package archives.tater.penchant;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;

import java.util.function.Function;
import java.util.stream.Stream;

public class PenchantUtil {
    private PenchantUtil() {}

    public static <T> Stream<Holder<T>> streamOrdered(HolderLookup<T> registry, TagKey<T> orderTag) {
        return registry.get(orderTag).map(holderSet -> Stream.concat(
                holderSet.stream(),
                registry.listElements()
                        .filter(holder -> !holderSet.contains(holder))
        )).orElseGet(() -> registry.listElements().map(Function.identity()));
    }
}
