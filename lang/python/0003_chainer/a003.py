#!/usr/bin/env python
"""
https://qiita.com/chachay/items/052406176c55dd5b9a6a
"""


from base import *


class MLP(Chain):
    n_input = 10
    n_output = 1
    n_units = 5

    def __init__(self):
        super(MLP, self).__init__(
            l1 = L.Linear(self.n_input, self.n_units),
            l2 = L.LSTM(self.n_units, self.n_units),
            l3 = L.Linear(self.n_units, self.n_output),
            )

    def reset_state(self):
        self.l2.reset_state()

    def __call__(self, x):
        h1 = self.l1(x)
        h2 = self.l2(h1)
        return self.l3(h2)

class LossFuncL(Chain):
    def __init__(self, predictor):
        super(LossFuncL, self).__init__(predictor=predictor)

    def __call__(self, x, t):
        x.data = x.data.reshape((-1, 1)).astype(np.float32)
        t.data = t.data.reshape((-1, 1)).astype(np.float32)

        y = self.predictor(x)
        loss = F.mean_squared_error(y, t)
        report({'loss':loss}, self)
        return loss


model = LossFuncL(MLP())
optimizer = optimizers.Adam()
optimizer.setup(model)


class LSTM_test_Iterator(chainer.dataset.Iterator):
    def __init__(self, dataset, batch_size=10, seq_len=5, repeat=True):
        self.seq_length = seq_len
        self.dataset = dataset
        self.nsamples = len(dataset)
        self.batch_size = batch_size
        self.repeat = repeat
        self.epoch = 0
        self.iteration = 0
        self.offsets = np.random.randint(0, len(dataset), size=batch_size)
        self.is_new_epoch = False

    def __next__(self):
        if not self.repeat and self.iteration*self.batch_size >= self.nsamples:
            raise StopIteration

        x, t = self.get_data()
        self.iteration += 1

        epoch = self.iteration // self.batch_size
        self.is_new_epoch = self.epoch < epoch
        if self.is_new_epoch:
            self.epoch = epoch
            self.offsets = np.random.randint(0, self.nsamples, size=self.batch_size)

        return list(zip(x, t))

    @property
    def epoch_detail(self):
        return self.iteration* self.batch_size/len(self.dataset)

    def get_data(self):
        tmp0 = [self.dataset[(offset + self.iteration)%self.nsamples][0] 
            for offset in self.offsets]
        tmp1 = [self.dataset[(offset + self.iteration+1)%self.nsamples][0] 
            for offset in self.offsets]
        return tmp0, tmp1

    def serialzie(self, serializer):
        self.iteration = serializer('iteration', self.iteration)
        self.epoch     = serializer('epoch', self.epoch)

class LSTM_updater(training.StandardUpdater):
    def __init__(self, train_iter, optimizer, device):
        super(LSTM_updater, self).__init__(train_iter, optimizer, device=device)
        self.seq_length = train_iter.seq_length

    def update_core(self):
        loss = 0
        train_iter = self.get_iterator('main')
        optimizer = self.get_optimizer('main')

        for i in range(self.seq_length):
            batch = np.array(train_iter.__next__()).astype(np.float32)
            x = batch[:,0].reshape((-1, 1))
            t = batch[:,1].reshape((-1, 1))
            loss += optimizer.target(Variable(x), Variable(t))

        optimizer.target.zerograds()
        loss.backward()
        loss.unchain_backward()
        optimizer.update()


in_data = pd.read_csv("testdata/USD_JPY_move_avg.csv",
    names=["date", "cur", "ask", "bid", "mv_ask", "mv_bid"
            , "p_date", "p_ask", "p_bid"],
    dtype={"ask":np.float32, "bid":np.float32
           , "mv_ask":np.float32, "mv_bid":np.float32
           , "p_ask":np.float32, "p_bid":np.float32},
    parse_dates=["date","p_date"]
    )

N_data = len(in_data)
N_Loop = 3
t = in_data.date

X = in_data.ask 

N_train = int(N_data*0.8)
N_test  = N_data - N_train
tmp_DataSet_X = np.array(X).astype(np.float32)
x_train = np.array(tmp_DataSet_X[:N_train])
x_test  = np.array(tmp_DataSet_X[N_train:])

train = tuple_dataset.TupleDataset(x_train)
test  = tuple_dataset.TupleDataset(x_test)

train_iter = LSTM_test_Iterator(train, batch_size = 10, seq_len = 10)
test_iter  = LSTM_test_Iterator(test,  batch_size = 10, seq_len = 10, repeat = False)

updater = LSTM_updater(train_iter, optimizer, -1)
trainer = training.Trainer(updater, (1000, 'epoch'), out = 'result')

## eval_model = model.copy()
## eval_rnn = eval_model.predictor
## eval_rnn.train = False
## trainer.extend(extensions.Evaluator(
##         test_iter, eval_model, device=-1,
##                 eval_hook=lambda _: eval_rnn.reset_state()))
## 
## trainer.extend(extensions.LogReport())
## 
## trainer.extend(
##         extensions.PrintReport(
##                 ['epoch', 'main/loss', 'validation/main/loss']
##                             )
##                 )
## 
## trainer.extend(extensions.ProgressBar())

trainer.run()


presteps = 10
model.predictor.reset_state()

for i in range(presteps):
    y = model.predictor(chainer.Variable(np.roll(x_train,i).reshape((-1,1))))

switch_flg = False
switch_flg = True
if switch_flg:
    print(np.roll(y.data, -presteps))
    print(x_train)
else:
    plt.plot(t[:N_train],np.roll(y.data,-presteps))
    plt.plot(t[:N_train],x_train)
    plt.savefig("fig/a003_{0}.png".format(presteps))
    ### plt.show()
