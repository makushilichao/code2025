<template>
    <div :class="{ 'dark-mode': isDark }">
        <div class="card" style="margin-left: -5px"><el-icon style="margin-right: 10px; align-items: center; font-size: 13px"><BellFilled /></el-icon>您好，{{ data.user.name }}，欢迎使用本系统！</div>
        <div class="card-container">
            <div class="card half-width" style="margin-top: 10px; margin-left: -5px">
                <div class="card-title" style="margin-bottom: 15px">
                    <div class="card-title-text">
                        <div class="card-title-text-title">
                            <i class="el-icon-time"></i>
                            <span style="font-size: 20px">系统公告</span>
                        </div>
                    </div>
                </div>
                <!-- 为 el-timeline 添加自定义类名 -->
                <el-timeline class="scrollable-timeline" style="max-width: 600px; height: 380px">
                    <el-timeline-item :timestamp="item.createTime" color="#0bbd87" placement="top"
                                      v-for="item in data.noticeData" :key="item.id">
                        <h4>{{ item.title }}</h4>
                        <p>{{ item.content }}</p>
                    </el-timeline-item>
                </el-timeline>
            </div>

            <div class="card half-width" style="margin-top: 10px">
                <div class="card-title" style="margin-bottom: 15px">
                    <div class="card-title-text">
                        <div class="card-title-text-title">
                            <i class="el-icon-time"></i>
                            <span style="font-size: 20px">数量排行</span>
                        </div>
                    </div>
                </div>
                <div id="bar" style="width: 100%; height: 380px;"></div>
            </div>

        </div>

        <div class="card-container">
            <div class="card half-width" style="margin-top: 10px; ; margin-left: -5px">
                <div class="card-title" style="margin-bottom: 15px">
                    <div class="card-title-text">
                        <div class="card-title-text-title">
                            <i class="el-icon-time"></i>
                            <span style="font-size: 20px">热点名词</span>
                        </div>
                    </div>
                </div>
                <div id="pie" style="width: 100%; height: 380px;"></div>
            </div>

            <div class="card half-width" style="margin-top: 10px">
                <div class="card-title" style="margin-bottom: 15px">
                    <div class="card-title-text">
                        <div class="card-title-text-title">
                            <i class="el-icon-time"></i>
                            <span style="font-size: 20px">数量排行</span>
                        </div>
                    </div>
                </div>
                <div id="line" style="width: 100%; height: 380px;"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, onMounted, onUnmounted, watch } from "vue";
import request from "../../utils/request.js";
import { ElMessage } from "element-plus";
import * as echarts from "echarts";
import { useDark, useToggle } from '@vueuse/core'

const isDark = useDark()
const toggleDark = useToggle(isDark)

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    noticeData: [],
    form: {}
});

const getNotice = async () => {
    try {
        const res = await request.get('/notice/selectAll');
        if (res.code === '200') {
            data.noticeData = res.data.slice(0, 3);
        } else {
            ElMessage.error(res.msg);
        }
    } catch (error) {
        ElMessage.error('获取公告数据失败');
    }
};

const loadChart = async (url, chartId, option) => {
    try {
        const res = await request.get(url);
        if (res.code === '200') {
            const chartDom = document.getElementById(chartId);
            const myChart = echarts.init(chartDom);
            if (chartId === 'pie') {
                option.series[0].data = res.data;
            } else {
                option.xAxis.data = res.data.xAxis;
                option.series[0].data = res.data.yAxis;
            }
            myChart.setOption(option);
            return {
                chart: myChart,
                option: option
            };
        } else {
            ElMessage.error(res.msg);
        }
    } catch (error) {
        ElMessage.error('获取图表数据失败');
    }
};

const loadPie = () => loadChart('/echarts/pie', 'pie', optionPie);
const loadBar = () => loadChart('/echarts/bar', 'bar', optionBar);
const loadLine = () => loadChart('/echarts/line', 'line', optionLine);

let pieChartInfo, barChartInfo, lineChartInfo;

onMounted(async () => {
    await getNotice();
    pieChartInfo = await loadPie();
    barChartInfo = await loadBar();
    lineChartInfo = await loadLine();
});

onUnmounted(() => {
    if (pieChartInfo) pieChartInfo.chart.dispose();
    if (barChartInfo) barChartInfo.chart.dispose();
    if (lineChartInfo) lineChartInfo.chart.dispose();
});

const optionPie = {
    title: {
        text: '不同分类下用户发布论坛文章的数量',
        subtext: '统计维度：文章分类',
        left: 'center',
        textStyle: { color: isDark.value ? '#fff' : '#333' },
        subtextStyle: { color: isDark.value ? '#ccc' : '#666' }
    },
    tooltip: {
        trigger: 'item'
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        textStyle: { color: isDark.value ? '#fff' : '#333' }
    },
    series: [
        {
            name: '数量占比',
            type: 'pie',
            radius: ['40%', '70%'],
            data: [],
            padAngle: 5,
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 10
            },
            label: {
                show: false,
                position: 'center'
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: 40,
                    fontWeight: 'bold'
                }
            },
            labelLine: {
                show: false
            },
        }
    ]
};

const optionBar = {
    title: {
        text: '不同用户发布论坛文章数量Top5',
        subtext: '统计维度：用户昵称',
        left: 'center',
        textStyle: { color: isDark.value ? '#fff' : '#333' },
        subtextStyle: { color: isDark.value ? '#ccc' : '#666' }
    },
    tooltip: {
        trigger: 'item'
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        data: [],
        axisTick: {
            alignWithLabel: true
        },
        axisLabel: { color: isDark.value ? '#fff' : '#333' }
    },
    yAxis: {
        name: '文章数量',
        type: 'value',
        axisLabel: { color: isDark.value ? '#fff' : '#333' }
    },
    series: [
        {
            data: [],
            type: 'bar',
            showBackground: true,
            backgroundStyle: {
                color: 'rgba(180, 180, 180, 0.2)'
            }
        }
    ]
};

const optionLine = {
    title: {
        text: '最近一周每天用户发布文章数量',
        subtext: '统计维度：最近一周',
        left: 'center',
        textStyle: { color: isDark.value ? '#fff' : '#333' },
        subtextStyle: { color: isDark.value ? '#ccc' : '#666' }
    },
    tooltip: {
        trigger: 'item'
    },
    xAxis: {
        name: '日期',
        type: 'category',
        data: [],
        axisTick: {
            alignWithLabel: true
        },
        axisLabel: { color: isDark.value ? '#fff' : '#333' }
    },
    yAxis: {
        name: '文章数量',
        type: 'value',
        axisLabel: { color: isDark.value ? '#fff' : '#333' }
    },
    series: [
        {
            data: [],
            type: 'line',
            smooth: true,
            markPoint: {
                data: [
                    {type: 'max', name: '最大值'},
                    {type: 'min', name: '最小值'}
                ]
            },
            markLine: {
                data: [
                    {type: 'average', name: '平均值'}
                ]
            }
        }
    ]
};

watch(isDark, () => {
    if (pieChartInfo) {
        pieChartInfo.option.title.textStyle.color = isDark.value ? '#fff' : '#333';
        pieChartInfo.option.title.subtextStyle.color = isDark.value ? '#ccc' : '#666';
        pieChartInfo.option.legend.textStyle.color = isDark.value ? '#fff' : '#333';
        pieChartInfo.chart.setOption(pieChartInfo.option);
    }
    if (barChartInfo) {
        barChartInfo.option.title.textStyle.color = isDark.value ? '#fff' : '#333';
        barChartInfo.option.title.subtextStyle.color = isDark.value ? '#ccc' : '#666';
        barChartInfo.option.xAxis.axisLabel.color = isDark.value ? '#fff' : '#333';
        barChartInfo.option.yAxis.axisLabel.color = isDark.value ? '#fff' : '#333';
        barChartInfo.chart.setOption(barChartInfo.option);
    }
    if (lineChartInfo) {
        lineChartInfo.option.title.textStyle.color = isDark.value ? '#fff' : '#333';
        lineChartInfo.option.title.subtextStyle.color = isDark.value ? '#ccc' : '#666';
        lineChartInfo.option.xAxis.axisLabel.color = isDark.value ? '#fff' : '#333';
        lineChartInfo.option.yAxis.axisLabel.color = isDark.value ? '#fff' : '#333';
        lineChartInfo.chart.setOption(lineChartInfo.option);
    }
});
</script>

<style scoped>
.card-container {
    display: flex;
    justify-content: space-between;
}

.half-width {
    width: 50%;
}

/* 自定义滚动条样式 */
.scrollable-timeline {
    overflow-y: auto;
    /* 滚动条宽度 */
    scrollbar-width: thin;
    /* 滚动条颜色 */
    scrollbar-color: #888 #f1f1f1;
}

/* 为暗夜模式下的滚动条设置不同颜色 */
.dark-mode .scrollable-timeline {
    scrollbar-color: #666 #333;
}

/* WebKit 浏览器滚动条样式 */
.scrollable-timeline::-webkit-scrollbar {
    width: 8px;
}

.scrollable-timeline::-webkit-scrollbar-track {
    background: #f1f1f1;
}

.dark-mode .scrollable-timeline::-webkit-scrollbar-track {
    background: #333;
}

.scrollable-timeline::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 4px;
}

.dark-mode .scrollable-timeline::-webkit-scrollbar-thumb {
    background: #666;
}

.scrollable-timeline::-webkit-scrollbar-thumb:hover {
    background: #555;
}

.dark-mode .scrollable-timeline::-webkit-scrollbar-thumb:hover {
    background: #444;
}
</style>