function [ distance ] = DistanceModel(n,a, dBm, f)
Ptx = 28; 
%Prx = 0;
f = f*1E6; %Megahertz
lambda = (3E8)/f;
Gtx = 2.5;
Grx = 2.5;
X = normrnd(1,a);
distance = power(10, (Ptx-dBm+Gtx+Grx-X+20*log10(lambda)-20*log10(4*pi))/(10*n));
end

