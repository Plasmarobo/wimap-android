function Aggrigate
data(1,:,:) = beacon10_fe_ed_b2_22_7c();
data(2,:,:) = beacon10_fe_ed_d1_aa_47();
data(3,:,:) = beaconf8_1a_67_ee_fd_ee();
figure(1);
hold on;
for i = 1:size(data,1)
    PlotPattern(data(i, :, :));
end
fspl = 0-20*log10(1:size(data,2));
plot(1:size(data,2), fspl)
title('average')
xlabel('meters')
ylabel('dbm')

end