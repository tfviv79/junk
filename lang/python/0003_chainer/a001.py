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
x = Variable(np.array([[1,2,3,4]], dtype=np.float32))
t = Variable(np.array([[0,1]], dtype=np.float32))
y = model(x)
optimizer = optimizers.SGD()
optimizer.setup(model)

model.cleargrads()
loss = F.sum(y-t)
loss.backward()
optimizer.update()
print("{0}".format(model(x)))

