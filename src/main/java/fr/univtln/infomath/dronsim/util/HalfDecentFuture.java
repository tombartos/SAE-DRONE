package fr.univtln.infomath.dronsim.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

class HalfDecentFuture<T> implements Future<T>, CompletionStage<T> {
    private CompletableFuture<T> cf;

    public HalfDecentFuture() {
        this.cf = new CompletableFuture<>();
    }

    private HalfDecentFuture(CompletableFuture<T> cf) {
        this.cf = cf;
    }

    public static <T> HalfDecentFuture<T> from(CompletableFuture<T> cf) {
        return new HalfDecentFuture<>(cf);
    }

    public HalfDecentFuture<T> thenCall(Callable<T> callable) {
        var cf = new CompletableFuture<T>();
        this.cf.thenAccept(x -> {
            try {
                cf.complete(callable.call());
            } catch (Exception e) {
                cf.completeExceptionally(e);
            }
        });
        return from(cf);
    }

    public HalfDecentFuture<T> thenCallAsync(Callable<T> callable) {
        var cf = new CompletableFuture<T>();
        this.cf.thenAcceptAsync(x -> {
            try {
                cf.complete(callable.call());
            } catch (Exception e) {
                cf.completeExceptionally(e);
            }
        });
        return from(cf);
    }

    //@SafeVarargs // can only throw cast exception if one of the hdfs is a raw type
    //public static <T> HalfDecentFuture<T> allOf(HalfDecentFuture<T>... hdfs) {
    //    var cfs = new CompletableFuture[hdfs.length];
    //    var cf = CompletableFuture.allOf(cfs).;
    //    return of(cf);
    //}

    @SafeVarargs // can only throw cast exception if one of the hdfs is a raw type
    public static <T> HalfDecentFuture<T> anyOf(HalfDecentFuture<T>... hdfs) {
        @SuppressWarnings("unchecked") // this is an array of nulls anyways
        var cfs = (CompletableFuture<T>[]) new CompletionStage[hdfs.length];

        for (int i = 0; i < cfs.length; ++i) {
            cfs[i] = hdfs[i].cf;
        }

        @SuppressWarnings("unchecked") // we know cfs all yeild T
        var cf = (CompletableFuture<T>) CompletableFuture.anyOf(cfs);

        return from(cf);
    }

    @Override
    public HalfDecentFuture<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return from(cf.acceptEither(other, action));
    }

    @Override
    public HalfDecentFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return from(cf.acceptEitherAsync(other, action));
    }

    @Override
    public HalfDecentFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action,
            Executor executor) {
        return from(cf.acceptEitherAsync(other, action, executor));
    }

    @Override
    public <U> HalfDecentFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return from(cf.applyToEither(other, fn));
    }

    @Override
    public <U> HalfDecentFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return from(cf.applyToEitherAsync(other, fn));
    }

    @Override
    public <U> HalfDecentFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn,
            Executor executor) {
        return from(cf.applyToEitherAsync(other, fn, executor));
    }

    @Override
    public HalfDecentFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return from(cf.exceptionally(fn));
    }

    @Override
    public <U> HalfDecentFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return from(cf.handle(fn));
    }

    @Override
    public <U> HalfDecentFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return from(cf.handleAsync(fn));
    }

    @Override
    public <U> HalfDecentFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return from(cf.handleAsync(fn, executor));
    }

    @Override
    public HalfDecentFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return from(cf.runAfterBoth(other, action));
    }

    @Override
    public HalfDecentFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return from(cf.runAfterBothAsync(other, action));
    }

    @Override
    public HalfDecentFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return from(cf.runAfterBothAsync(other, action, executor));
    }

    @Override
    public HalfDecentFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return from(cf.runAfterEither(other, action));
    }

    @Override
    public HalfDecentFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return from(cf.runAfterEitherAsync(other, action));
    }

    @Override
    public HalfDecentFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return from(cf.runAfterEitherAsync(other, action, executor));
    }

    @Override
    public HalfDecentFuture<Void> thenAccept(Consumer<? super T> action) {
        return from(cf.thenAccept(action));
    }

    @Override
    public HalfDecentFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
        return from(cf.thenAcceptAsync(action));
    }

    @Override
    public HalfDecentFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return from(cf.thenAcceptAsync(action, executor));
    }

    @Override
    public <U> HalfDecentFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        return from(cf.thenAcceptBoth(other, action));
    }

    @Override
    public <U> HalfDecentFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        return from(cf.thenAcceptBothAsync(other, action));
    }

    @Override
    public <U> HalfDecentFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action, Executor executor) {
        return from(cf.thenAcceptBothAsync(other, action, executor));
    }

    @Override
    public <U> HalfDecentFuture<U> thenApply(Function<? super T, ? extends U> fn) {
        return from(cf.thenApply(fn));
    }

    @Override
    public <U> HalfDecentFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return from(cf.thenApplyAsync(fn));
    }

    @Override
    public <U> HalfDecentFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return from(cf.thenApplyAsync(fn, executor));
    }

    @Override
    public <U, V> HalfDecentFuture<V> thenCombine(CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        return from(cf.thenCombine(other, fn));
    }

    @Override
    public <U, V> HalfDecentFuture<V> thenCombineAsync(CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        return from(cf.thenCombineAsync(other, fn));
    }

    @Override
    public <U, V> HalfDecentFuture<V> thenCombineAsync(CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return from(cf.thenCombineAsync(other, fn, executor));
    }

    @Override
    public <U> HalfDecentFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return from(cf.thenCompose(fn));
    }

    @Override
    public <U> HalfDecentFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        return from(cf.thenComposeAsync(fn));
    }

    @Override
    public <U> HalfDecentFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn,
            Executor executor) {
        return from(cf.thenComposeAsync(fn, executor));
    }

    @Override
    public HalfDecentFuture<Void> thenRun(Runnable action) {
        return from(cf.thenRun(action));
    }

    @Override
    public HalfDecentFuture<Void> thenRunAsync(Runnable action) {
        return from(cf.thenRunAsync(action));
    }

    @Override
    public HalfDecentFuture<Void> thenRunAsync(Runnable action, Executor executor) {
        return from(cf.thenRunAsync(action, executor));
    }

    @Override
    public CompletableFuture<T> toCompletableFuture() {
        return cf.toCompletableFuture();
    }

    public HalfDecentFuture<T> toHalfDecentFuture() {
        return from(cf.toCompletableFuture());
    }

    @Override
    public HalfDecentFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return from(cf.whenComplete(action));
    }

    @Override
    public HalfDecentFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return from(cf.whenCompleteAsync(action));
    }

    @Override
    public HalfDecentFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return from(cf.whenCompleteAsync(action, executor));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return cf.cancel(mayInterruptIfRunning);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return cf.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return cf.get(timeout, unit);
    }

    @Override
    public boolean isCancelled() {
        return cf.isCancelled();
    }

    @Override
    public boolean isDone() {
        return cf.isDone();
    }
}
