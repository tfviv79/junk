
### ミニマムにデータを食わせて調整していくprogramベースにする

from base import *

class MLP(Chain):
    n_input = 9
    n_output = 1
    n_units = 5

    def __init__(self):
        super(MLP, self).__init__(
            i1 = L.Linear(self.n_input, self.n_units),
            l1 = L.LSTM(self.n_units, self.n_units),
            l2 = L.LSTM(self.n_units, self.n_units),
            l3 = L.LSTM(self.n_units, self.n_units),
            o1 = L.Linear(self.n_units, self.n_output),
            )

    def reset_state(self):
        self.l1.reset_state()
        self.l2.reset_state()

    def __call__(self, x):
        h1 = self.i1(x)
        ld = h1
        for l in [
                self.l1
                , self.l2
                #, self.l3
                ]:
            ld = l(ld)
        h4 = self.o1(ld)
        return h4

    def train(self, optimizer, x_data, y_data):
        x = Variable(x_data.reshape(1, MLP.n_input).astype(np.float32))
        y = Variable(y_data.reshape(1, MLP.n_output).astype(np.float32))
        h = self(x)

        self.cleargrads()
        error = F.mean_squared_error(h, y)
        error.backward()
        optimizer.update()

def apply_model(model, x_data):
    x = Variable(x_data.reshape(1, MLP.n_input).astype(np.float32))
    h = model(x)
    return h

def run(model, optimizer, data, n=100):
    optimizer.setup(model)
    for i, o in data:
        model.train(optimizer, i, o)

in_data = pd.read_csv("testdata/USD_JPY_move_avg.min.csv",
    names=["date", "cur", "ask", "bid", "mv_ask", "mv_bid"
            , "p_date", "p_ask", "p_bid"],
    dtype={"ask":np.float32, "bid":np.float32
           , "mv_ask":np.float32, "mv_bid":np.float32
           , "p_ask":np.float32, "p_bid":np.float32},
    parse_dates=["date","p_date"]
    )

def pdata(X, start, length):
    end_pos = start + length
    v_x = np.array(X[start:end_pos]) - np.array(X[start+1:end_pos + 1])
    tmp_DataSet_X = v_x.reshape((-1, 1)).astype(np.float32)

    v_y = np.array(X[end_pos:end_pos + 1]) - np.array(X[end_pos+1 : end_pos + 2])
    tmp_DataSet_Y = v_y.reshape((-1, 1)).astype(np.float32)
    return tmp_DataSet_X, tmp_DataSet_Y

def atrain(start, length):
    x, t = pdata(in_data.ask, start, length)
    return [(x, t)]

def r(data, model=None, n=100):
    if not model :
        model = MLP()
    optimizer = optimizers.MomentumSGD(lr=0.01, momentum=0.9)
    run(model, optimizer, data, n)
    x_vals = []
    t_vals = []
    y_vals = []
    for i, o in data:
        y = apply_model(model, i).data
        t_vals.append(i)
        x_vals.append(o)
        y_vals.append(y)
        print("chec: {} <--> {}".format(y, o))
    #out_data(t_vals, x_vals, y_vals, len(t_vals))
    return model


def out_data(t, x_train, y, suffix):
    plt.plot(t,y)
    plt.plot(t,x_train)
    plt.savefig("fig/a005_{0}.png".format(suffix))

def main(trainnum=5, md=None):
    return r(atrain(0, MLP.n_input), n=trainnum, model=md)

if __name__ == "__main__":
    main()
