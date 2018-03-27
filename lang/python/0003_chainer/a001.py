from base import *

class MyChain(Chain):
    def __init__(self):
        super(MyChain, self).__init__()
        with self.init_scope():
            self.l1 = L.Linear(4, 3)
            self.l2 = L.Linear(3, 2)

    def __call__(self, x):
        h = self.l1(x)
        return self.l2(h)

model = MyChain()
optimizer = optimizers.SGD()
optimizer.setup(model)

x = np.random.uniform(-1, 1, (2, 4)).astype('f')
model.cleargrads()
loss = F.sum(model(chainer.Variable(x)))
loss.backward()
optimizer.update()

