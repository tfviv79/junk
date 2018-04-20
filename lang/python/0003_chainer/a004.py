

from base import *

## http://hi-king.hatenablog.com/entry/2015/06/27/194630:w

class SmallClassificationModel(Chain):
    def __init__(self):
        super(SmallClassificationModel, self).__init__(
            fc1 = L.Linear(2,2)
            )
    def __call__(self, x):
        h = self.fc1(x)
        #print("x0: {}, h0: {}".format(x.data, h.data))
        return h

    def train(self, optimizer, x_data, y_data):
        x = Variable(x_data.reshape(1, 2).astype(np.float32))
        y = Variable(y_data.astype(np.int32))
        h = self(x)
        #print("h0: {}".format(h.data))
        

        self.cleargrads()
        error = F.softmax_cross_entropy(h, y)

        error.backward()
        optimizer.update()

        #print("x: {}  --> h_class: {}".format(x.data, h.data.argmax()))
        #print("  h: {}".format(h.data))

class ClassificationModel(Chain):
    def __init__(self):
        super(ClassificationModel, self).__init__(
            fc1 = L.Linear(2,2),
            fc2 = L.Linear(2,2)
            )
    def __call__(self, x):
        h = F.sigmoid(self.fc1(x))
        y = self.fc2(h)
        #print("x0: {}, h0: {}".format(x.data, h.data))
        return y

    def train(self, optimizer, x_data, y_data):
        x = Variable(x_data.reshape(1, 2).astype(np.float32))
        y = Variable(y_data.astype(np.int32))
        h = self(x)

        self.cleargrads()
        error = F.softmax_cross_entropy(h, y)
        error.backward()
        optimizer.update()

        #print("x: {}  --> h_class: {}".format(x.data, h.data.argmax()))
        #print("  h: {}".format(h.data))

class RegressionModel(Chain):
    def __init__(self):
        super(RegressionModel, self).__init__(
            fc1 = L.Linear(2,2),
            fc2 = L.Linear(2,1)
            )
    def __call__(self, x):
        h = F.sigmoid(self.fc1(x))
        y = self.fc2(h)
        #print("x0: {}, h0: {}".format(x.data, h.data))
        return y

    def train(self, optimizer, x_data, y_data):
        x = Variable(x_data.reshape(1, 2).astype(np.float32))
        y = Variable(y_data.reshape(1, 1).astype(np.float32))
        h = self(x)

        self.cleargrads()
        error = F.mean_squared_error(h, y)
        error.backward()
        optimizer.update()

        #print("x: {}  --> h_class: {}".format(x.data, h.data.argmax()))
        #print("  h: {}   err: {}".format(h.data, error))

class MLP(Chain):
    n_input = 2
    n_output = 1
    n_units = 5

    def __init__(self):
        super(MLP, self).__init__(
            i1 = L.Linear(self.n_input, self.n_units),
            l1 = L.LSTM(self.n_units, self.n_units),
            l2 = L.LSTM(self.n_units, self.n_units),
            o1 = L.Linear(self.n_units, self.n_output),
            )

    def reset_state(self):
        self.l1.reset_state()
        self.l2.reset_state()

    def __call__(self, x):
        h1 = self.i1(x)
        h2 = self.l1(h1)
        h3 = self.l2(h2)
        h4 = self.o1(h3)
        return h4

    def train(self, optimizer, x_data, y_data):
        x = Variable(x_data.reshape(1, 2).astype(np.float32))
        y = Variable(y_data.reshape(1, 1).astype(np.float32))
        h = self(x)

        self.cleargrads()
        error = F.mean_squared_error(h, y)
        error.backward()
        optimizer.update()

def apply_model(model, x_data):
    x = Variable(x_data.reshape(1, 2).astype(np.float32))
    h = model(x)
    return h

def run(model, optimizer, data=data_and, n=100):
    optimizer.setup(model)
    for i, o in data*n:
        model.train(optimizer, i, o)

def r(mode, data, n=100):
    if mode == 1:
        model = SmallClassificationModel()
    elif mode == 2:
        model = ClassificationModel()
    elif mode == 3:
        model = RegressionModel()
    elif mode == 4:
        model = MLP()
    optimizer = optimizers.MomentumSGD(lr=0.01, momentum=0.9)
    run(model, optimizer, data, n)
    for i, o in data:
        print("{}: {} {}".format(i, apply_model(model, i).data, o))
    return model
    


data_and = [
    [np.array([0,0]), np.array([0])], 
    [np.array([0,1]), np.array([0])], 
    [np.array([1,0]), np.array([0])], 
    [np.array([1,1]), np.array([1])], 
]

data_xor = [
    [np.array([0,0]), np.array([0])], 
    [np.array([0,1]), np.array([1])], 
    [np.array([1,0]), np.array([1])], 
    [np.array([1,1]), np.array([0])], 
]


if __name__ == "__main__":
    optimizer = optimizers.MomentumSGD(lr=0.01, momentum=0.9)
    run(1000)
