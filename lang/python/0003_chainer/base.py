import numpy as np
import chainer
from chainer import cuda, Function, gradient_check, report, training, utils, Variable
from chainer import datasets, iterators, optimizers, serializers
from chainer import Link, Chain, ChainList
import chainer.functions as F
import chainer.links as L
from chainer.training import extensions

from chainer import report
from chainer.datasets import tuple_dataset
import matplotlib as mpl
mpl.use("Agg")
import matplotlib.pyplot as plt


import pandas as pd
