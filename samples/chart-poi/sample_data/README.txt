https://qiita.com/kmjtr/items/8443144240c634465162
    で紹介されていた、
    https://users.nber.org/~rdehejia/data/cps_controls.dta
    を使う。
    が、pandaフォーマット？みたいなので、SQLで扱えるように加工する。

    データ自体は以下の論文からきているようです。
    Rajeev Dehejia and Sadek Wahba, 
    "Causal Effects in Non-Experimental Studies: Reevaluating the Evaluation of Training Programs,"
    Journal of the American Statistical Association, Vol. 94, No. 448 (December 1999), pp. 1053-1062.

    Rajeev Dehejia and Sadek Wahba, 
    "Propensity Score Matching Methods for Non-Experimental Causal Studies,"
    Review of Economics and Statistics, Vol. 84, (February 2002), pp. 151-161.

    Robert Lalonde, 
    "Evaluating the Econometric Evaluations of Training Programs,"
    American Economic Review, Vol. 76 (1986), pp. 604-620. 

データ内容。
The variables from left to right are: 
- treatment indicator (1 if treated, 0 if not treated),
- age,
- education,
- Black (1 if black, 0 otherwise),
- Hispanic (1 if Hispanic, 0 otherwise),
- married (1 if married, 0 otherwise),
- nodegree (1 if no degree, 0 otherwise),
- RE74 (earnings in 1974),
- RE75 (earnings in 1975),
- and RE78 (earnings in 1978).
