package llparser.utils;

import java.util.function.Function;

public class Result<Ok, Ng> {
    private final boolean isOk;
    private final Ok ok;
    private final Ng ng;

    private Result(boolean isOk, Ok ok, Ng ng) {
        this.isOk = isOk;
        this.ok = ok;
        this.ng = ng;
    }

    public Ok ok() {
        return ok;
    }

    public Ng ng() {
        return ng;
    }

    public <Ok2> Result<Ok2, Ng> map(Function<Ok, Ok2> apply) {
        if (isOk) {
            return ok(apply.apply(ok));
        } else {
            return ng(ng);
        }
    }

    public <Ok2> Result<Ok2, Ng> then(Function<Ok, Result<Ok2, Ng>> apply) {
        if (isOk) {
            return apply.apply(ok);
        } else {
            return ng(ng);
        }
    }

    public <Ng2> Result<Ok, Ng2> mapErr(Function<Ng, Ng2> apply) {
        if (isOk) {
            return ok(ok);
        } else {
            return ng(apply.apply(ng));
        }
    }

    public <Ng2> Result<Ok, Ng2> err(Function<Ng, Result<Ok, Ng2>> apply) {
        if (isOk) {
            return ok(ok);
        } else {
            return apply.apply(ng);
        }
    }

    public boolean isOk() {
        return isOk;
    }

    public boolean isErr() {
        return !isOk();
    }

    public static <Ok2, Ng2> Result<Ok2, Ng2> ok(Ok2 o) {
        return new Result<Ok2, Ng2>(true, o, null);
    }
    public static <Ok2, Ng2> Result<Ok2, Ng2> ng(Ng2 n) {
        return new Result<Ok2, Ng2>(false, null, n);
    }
}
