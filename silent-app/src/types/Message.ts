export type Message = {
  id: number;
  title: string;
  content: string;
  daysToTrigger: number;
  recipients: string[];
  active: boolean
};
